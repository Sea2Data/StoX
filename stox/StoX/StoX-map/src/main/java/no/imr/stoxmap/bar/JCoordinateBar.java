/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.bar;

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import no.imr.stox.util.map.LatLonUtil;
import no.imr.stox.util.base.Conversion;
import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.AbstractMapControlBar;
import org.geotoolkit.gui.swing.render2d.control.JAdditionalAxisNavigator;
import org.geotoolkit.gui.swing.render2d.control.JCRSButton;
import org.geotoolkit.gui.swing.render2d.control.JScaleCombo;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import no.imr.stoxmap.utils.ICoordinateViewer;
import no.imr.stoxmap.utils.ProjectionUtils;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JCoordinateBar extends AbstractMapControlBar implements ICoordinateViewer {

    private static final ImageIcon ICON_HINT = IconBuilder.createIcon(FontAwesomeIcons.ICON_LIGHTBULB, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_DIMENSIONS = IconBuilder.createIcon(FontAwesomeIcons.ICON_TASKS, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_FRAME = IconBuilder.createIcon(FontAwesomeIcons.ICON_EXTERNAL_LINK, 16, FontAwesomeIcons.DEFAULT_COLOR);

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    private final myListener listener = new myListener();
    private String error = MessageBundle.getString("map_control_coord_error");

    private final JButton guiHint = new JButton(ICON_HINT);
    private final JScaleCombo guiCombo = new JScaleCombo();
    private final JTextField guiCoord = new JTextField();
    private final JCRSButton guiCRS = new JCRSButton();
    private final JToggleButton guiDimensions = new JToggleButton(ICON_DIMENSIONS);

    private final JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private final JPanel paneTemp = new JPanel(new BorderLayout());
    private final JAdditionalAxisNavigator guiAdditional = new JAdditionalAxisNavigator();

    private final JToggleButton frameAction = new JToggleButton(new AbstractAction("", ICON_FRAME) {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateDimensionFrame();
        }
    });
    private JDialog dimensionDialog = null;

    public JCoordinateBar() {
        this(null);
    }

    public JCoordinateBar(final JMap2D candidate) {

        setLayout(new BorderLayout(0, 1));
        final JToolBar bottom = new JToolBar();
        bottom.setFloatable(false);
        bottom.setLayout(new GridBagLayout());
        add(BorderLayout.SOUTH, bottom);

        paneTemp.add(BorderLayout.CENTER, guiAdditional);
        paneTemp.setPreferredSize(new Dimension(120, 120));

        //the hints menu -------------------------------------------------------
        final JCheckBoxMenuItem guiAxis = new JCheckBoxMenuItem(MessageBundle.getString("map_xy_ratio")) {
            @Override
            public boolean isSelected() {
                if (map == null) {
                    return false;
                }
                return map.getCanvas().getAxisProportions() == 1;
            }
        };
        guiAxis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (map != null) {
                    double d = map.getCanvas().getAxisProportions();
                    map.getCanvas().setAxisProportions((d == 1) ? Double.NaN : 1);
                }
            }
        });

        final JCheckBoxMenuItem guiStyleOrder = new JCheckBoxMenuItem(MessageBundle.getString("map_style_order")) {
            @Override
            public boolean isSelected() {
                if (map == null) {
                    return false;
                }
                return GO2Hints.SYMBOL_RENDERING_PRIME.equals(
                        map.getCanvas().getRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER));
            }
        };
        guiStyleOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (map != null) {
                    final Object val = map.getCanvas().getRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER);
                    map.getCanvas().setRenderingHint(
                            GO2Hints.KEY_SYMBOL_RENDERING_ORDER, (GO2Hints.SYMBOL_RENDERING_PRIME.equals(val))
                            ? GO2Hints.SYMBOL_RENDERING_SECOND : GO2Hints.SYMBOL_RENDERING_PRIME);
                }
            }
        });

        final JCheckBoxMenuItem guiAntiAliasing = new JCheckBoxMenuItem(MessageBundle.getString("antialiasing")) {
            @Override
            public boolean isSelected() {
                if (map == null) {
                    return false;
                }
                return RenderingHints.VALUE_ANTIALIAS_ON.equals(
                        map.getCanvas().getRenderingHint(RenderingHints.KEY_ANTIALIASING));
            }
        };
        guiAntiAliasing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (map != null) {
                    final Object val = map.getCanvas().getRenderingHint(RenderingHints.KEY_ANTIALIASING);
                    map.getCanvas().setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING, (RenderingHints.VALUE_ANTIALIAS_ON.equals(val))
                            ? RenderingHints.VALUE_ANTIALIAS_OFF : RenderingHints.VALUE_ANTIALIAS_ON);
                }
            }
        });

        final ButtonGroup group = new ButtonGroup();
        final JRadioButtonMenuItem guiNone = new JRadioButtonMenuItem(MessageBundle.getString("interpolation_none")) {
            @Override
            public boolean isSelected() {
                if (map == null) {
                    return false;
                }
                return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR.equals(
                        map.getCanvas().getRenderingHint(RenderingHints.KEY_INTERPOLATION));
            }
        };
        final JRadioButtonMenuItem guiLinear = new JRadioButtonMenuItem(MessageBundle.getString("interpolation_linear")) {
            @Override
            public boolean isSelected() {
                if (map == null) {
                    return false;
                }
                return RenderingHints.VALUE_INTERPOLATION_BILINEAR.equals(
                        map.getCanvas().getRenderingHint(RenderingHints.KEY_INTERPOLATION));
            }
        };
        final JRadioButtonMenuItem guiBicubic = new JRadioButtonMenuItem(MessageBundle.getString("interpolation_bicubic")) {
            @Override
            public boolean isSelected() {
                if (map == null) {
                    return false;
                }
                return RenderingHints.VALUE_INTERPOLATION_BICUBIC.equals(
                        map.getCanvas().getRenderingHint(RenderingHints.KEY_INTERPOLATION));
            }
        };
        group.add(guiNone);
        group.add(guiLinear);
        group.add(guiBicubic);
        final ActionListener interListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (map != null) {
                    final Object source = e.getSource();
                    if (source == guiNone) {
                        map.getCanvas().setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    } else if (source == guiLinear) {
                        map.getCanvas().setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    } else if (source == guiBicubic) {
                        map.getCanvas().setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    }
                }
            }
        };
        guiNone.addActionListener(interListener);
        guiLinear.addActionListener(interListener);
        guiBicubic.addActionListener(interListener);

        final JCheckBoxMenuItem guiMultiThread = new JCheckBoxMenuItem(MessageBundle.getString("multithread")) {
            @Override
            public boolean isSelected() {
                if (map == null) {
                    return false;
                }
                return GO2Hints.MULTI_THREAD_ON.equals(map.getCanvas().getRenderingHint(GO2Hints.KEY_MULTI_THREAD));
            }
        };
        guiMultiThread.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (map != null) {
                    final Object val = map.getCanvas().getRenderingHint(GO2Hints.KEY_MULTI_THREAD);
                    map.getCanvas().setRenderingHint(GO2Hints.KEY_MULTI_THREAD, (GO2Hints.MULTI_THREAD_ON.equals(val))
                            ? GO2Hints.MULTI_THREAD_OFF : GO2Hints.MULTI_THREAD_ON);
                }
            }
        });

        final JPopupMenu guiHintMenu = new JPopupMenu();
        guiHintMenu.add(guiAxis);
        guiHintMenu.add(guiMultiThread);
        guiHintMenu.add(guiStyleOrder);
        guiHintMenu.add(guiAntiAliasing);
        guiHintMenu.add(new JSeparator());
        guiHintMenu.add(new JMenuItem(MessageBundle.getString("interpolation")));
        guiHintMenu.add(guiNone);
        guiHintMenu.add(guiLinear);
        guiHintMenu.add(guiBicubic);

        guiHint.setComponentPopupMenu(guiHintMenu);
        guiHint.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON1) {
                    guiHintMenu.setSize(guiHintMenu.getPreferredSize());
                    final Dimension dim = guiHintMenu.getSize();
                    guiHintMenu.show(guiHint.getParent(), guiHint.getX(), guiHint.getY() - dim.height);
                }
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }
        });

        guiCRS.setEnabled(false);
        guiAxis.setOpaque(false);

        guiCombo.setOpaque(false);

        guiCoord.setOpaque(false);
        guiCoord.setBorder(null);
        guiCoord.setEditable(false);
        guiCoord.setHorizontalAlignment(SwingConstants.CENTER);

        final int defaultInsetTop = guiDimensions.getMargin().top;
        final int defaultInsetBottom = guiDimensions.getMargin().bottom;

        guiHint.setMargin(new Insets(defaultInsetTop, 0, defaultInsetBottom, 0));

        guiDimensions.setMargin(new Insets(defaultInsetTop, 0, defaultInsetBottom, 0));
        guiDimensions.setToolTipText(MessageBundle.getString("map_elevation_slider"));
        guiDimensions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paneTemp.setVisible(guiDimensions.isSelected());
                verticalSplit.setDividerLocation(baseMapContainer.getHeight() - paneTemp.getPreferredSize().height);
            }
        });

        guiAdditional.setPreferredSize(new Dimension(100, 100));
        guiAdditional.getToolbar().add(frameAction);

        int x = 1;

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 1;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.weightx = 0.0;
        constraints.weighty = 1.0;
        constraints.gridy = 0;

        /*constraints.gridx = x++;
        bottom.add(guiHint, constraints);
        constraints.gridx = x++;
        bottom.add(guiDimensions, constraints);*/
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.gridx = x++;
        bottom.add(guiCoord, constraints);

        /* constraints.weightx = 0;
        constraints.gridx = x++;
        bottom.add(guiCombo, constraints);*/
        constraints.weightx = 0;
        constraints.gridx = x++;
        bottom.add(guiCRS, constraints);

        paneTemp.setVisible(false);
        verticalSplit.setDividerSize(2);
        verticalSplit.setTopComponent(new JLabel());
        verticalSplit.setBottomComponent(paneTemp);

        setMap(candidate);
    }

    private Container baseMapContainer;
    private Component baseMapComponent;

    @Override
    public void setMap(final JMap2D map) {
        super.setMap(map);
        guiCombo.setMap(map);
        guiAdditional.setMap(map);

        if (baseMapContainer != null) {
            baseMapContainer.remove(verticalSplit);
            baseMapContainer.add(BorderLayout.CENTER, baseMapComponent);
            baseMapComponent.removeMouseMotionListener(listener);
            this.map.getCanvas().removePropertyChangeListener(listener);
        }

        this.map = map;
        guiCRS.setMap(this.map);

        if (this.map != null) {
            baseMapContainer = map.getUIContainer();
            baseMapComponent = map.getComponent(0);
            baseMapComponent.addMouseMotionListener(listener);
            this.map.getCanvas().addPropertyChangeListener(listener);
            map.getCanvas().addPropertyChangeListener(listener);

            baseMapContainer.remove(baseMapComponent);

            verticalSplit.setTopComponent(baseMapComponent);
            //multiSplitPane.setDividerSize(2);
            baseMapContainer.add(BorderLayout.CENTER, verticalSplit);
            baseMapContainer.repaint();

            final CoordinateReferenceSystem crs = map.getCanvas().getObjectiveCRS();
            guiCRS.setText(crs.getName().toString());
        }

        guiCRS.setEnabled(this.map != null);
    }

    public void setScales(final List<Number> scales) {
        guiCombo.setScales(scales);
    }

    public List<Number> getScales() {
        return guiCombo.getScales();
    }

    public void setStepSize(final Number step) {
        guiCombo.setStepSize(step);
    }

    public Number getStepSize() {
        return guiCombo.getStepSize();
    }

    private void updateDimensionFrame() {
        if (dimensionDialog != null) {
            dimensionDialog.setContentPane(new JPanel());
            dimensionDialog.setVisible(false);
            dimensionDialog.dispose();
        }
        paneTemp.remove(guiAdditional);

        if (frameAction.isSelected()) {
            paneTemp.setVisible(false);
            dimensionDialog = new JDialog();
            dimensionDialog.setContentPane(guiAdditional);
            dimensionDialog.setSize(guiAdditional.getSize().width, guiAdditional.getSize().height);
            dimensionDialog.setLocationRelativeTo(null);
            dimensionDialog.setVisible(true);

        } else {
            paneTemp.add(BorderLayout.CENTER, guiAdditional);
            paneTemp.setVisible(true);
            verticalSplit.setDividerLocation(baseMapContainer.getHeight() - paneTemp.getPreferredSize().height);
        }

        paneTemp.revalidate();
        paneTemp.repaint();
    }

    @Override
    public void updateCoord(final MouseEvent event) {
        Coordinate c = ProjectionUtils.getLatLonFromPoint(map, event.getPoint());
        final StringBuilder sb = new StringBuilder("  ");
        String s = Conversion.formatDoubletoDecimalString(c.y, 3) + ", " + Conversion.formatDoubletoDecimalString(c.x, 3) + 
                " (" + LatLonUtil.latLonToStr(c.y, c.x) + ")";
        sb.append(s);
        guiCoord.setText(sb.toString());
    }

    private class myListener extends MouseMotionAdapter implements PropertyChangeListener {

        @Override
        public void mouseMoved(final MouseEvent e) {
            updateCoord(e);
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            updateCoord(e);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent arg0) {
            if (J2DCanvas.OBJECTIVE_CRS_KEY.equals(arg0.getPropertyName())) {
                CoordinateReferenceSystem crs = map.getCanvas().getObjectiveCRS();
                guiCRS.setText(crs.getName().toString());
            } else if (AbstractCanvas.RENDERSTATE_KEY.equals(arg0.getPropertyName())) {
                final Object state = arg0.getNewValue();
                if (AbstractCanvas.ON_HOLD.equals(state)) {
                    map.getInformationDecoration().setPaintingIconVisible(false);
                } else if (AbstractCanvas.RENDERING.equals(state)) {
                    map.getInformationDecoration().setPaintingIconVisible(true);
                } else {
                    map.getInformationDecoration().setPaintingIconVisible(false);
                }
            }
        }

    }

    private static ImageIcon addHorizontalMargin(final ImageIcon icon, final int margin) {
        final Image img = icon.getImage();
        BufferedImage buffer = new BufferedImage(img.getWidth(null) + 2 * margin, img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        buffer.getGraphics().drawImage(img, margin, 0, null);
        return new ImageIcon(buffer);
    }

}
