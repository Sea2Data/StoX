/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.strataedit;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.util.LayerListRenderer;
import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * Adapted from JLayerComboBox to show only the strata layer.
 * @author aasmunds
 */
public class StrataLayerComboBox extends JList implements ContextListener{

    private final ContextListener.Weak weaklistener = new ContextListener.Weak(this);
    private JMap2D map = null;

    public StrataLayerComboBox() {
        this(null);
    }

    public StrataLayerComboBox(final JMap2D map){
        setCellRenderer(new LayerListRenderer());
        setMap(map);
    }

    public void setMap(JMap2D map) {
        if(map == this.map){
            return;
        }

        unregisterListener();
        this.map = map;
        registerListener();

        if(map != null){
            map.getContainer().addPropertyChangeListener(this);
        }

        reloadModel();
    }

    public JMap2D getMap() {
        return map;
    }

    private void registerListener(){
        final MapContext context = getContext();
        if(context != null){
            weaklistener.registerSource(context);
        }
    }

    private void unregisterListener(){
        weaklistener.unregisterAll();
    }

    private MapContext getContext(){
        if(map != null){
            final ContextContainer2D cc = map.getContainer();
            if(cc != null){
                return cc.getContext();
            }
        }
        return null;
    }

    private void reloadModel(){
        final MapContext context = getContext();
        final List<Object> objects = new ArrayList<>();

        if(context != null){
            for (MapLayer mapLayer : context.layers()) {
                if(!mapLayer.getName().equalsIgnoreCase("Strata")) {
                    continue;
                }
                if (mapLayer.isVisible() || mapLayer.isSelectable()) {
                    objects.add(mapLayer);
                    setSelectedIndex(0);
                    break; // Select strata
                }
            }
        }

        setModel(new ListComboBoxModel(objects));

        final Dimension minSize = getMinimumSize();
        if(minSize.width>150){
            minSize.width = 150;
            setMinimumSize(minSize);
        }

    }

    @Override
    public void layerChange(CollectionChangeEvent<MapLayer> event) {
        reloadModel();
    }

    @Override
    public void itemChange(CollectionChangeEvent<MapItem> event) {
        reloadModel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(ContextContainer2D.CONTEXT_PROPERTY.equals(evt.getPropertyName())){
            //map context changed
            unregisterListener();
            reloadModel();
            registerListener();
        }
    }


}
