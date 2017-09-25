package no.imr.stox.functions.utils;

import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.matrix.MatrixBO;

/**
 * Utility class for abundance estimation parameters
 *
 * @author Åsmund
 */
public final class AbndEstParamUtil {

    /**
     * Hidden constructor
     */
    private AbndEstParamUtil() {
    }

    /**
     * @param estLayerDef
     * @return estimation layer matrix from estimation layer definition
     */
    public static MatrixBO getEstLayerMatrixFromEstLayerDef(String estLayerDef) {
        MatrixBO estLayers = new MatrixBO(Functions.MM_DEFINEESTLAYER_MATRIX);
        String[] layers = estLayerDef.split("[\\s;]+");
        for (String layer : layers) {
            String[] namevalue = layer.split("~");
            estLayers.setRowValue(namevalue[0], namevalue[1]);
        }
        return estLayers;
    }

    /**
     * Lookup estimation layer from channel layer.
     *
     * @param estLayers Estimation layer definition
     * @param layer Layer of a type different from estimation layer
     * @return Estimation layer from channel layer
     */
    public static String getEstLayerFromLayer(MatrixBO estLayers, String layer) {
        if (layer == null) {
            return null;
        }
        if(estLayers == null) {
            return layer; // layer is a estimation layer already
        }
        for (String esKey : estLayers.getRowKeys()) {
            String estLayerDef = (String) estLayers.getRowValue(esKey);
            String[] tokens = estLayerDef.split("-");
            if (tokens.length == 1) {
                switch (layer) {
                    case Functions.DEPTHLAYER_BOT:
                    // 1~BOT
                    case Functions.DEPTHLAYER_PEL:
                    // 1~PEL
                    case Functions.WATERCOLUMN_PELBOT:
                    // 1~PELBOT
                        if (layer.equals(estLayerDef)) {
                            return esKey;
                        }
                        break;
                    default:
                       if(estLayerDef.equals(Functions.WATERCOLUMN_PELBOT)) {
                           // A Pchannel 1 is going into definition of PELBOT
                           return esKey;
                       }
                }
                continue;
            }
            if (tokens.length != 2) {
                continue;
            }
            // 1~1-2
            Integer min = Conversion.safeStringtoIntegerNULL(tokens[0]);
            Integer max = Conversion.safeStringtoIntegerNULL(tokens[1]);
            Integer chn = Conversion.safeStringtoIntegerNULL(layer);
            if (min == null || max == null || chn == null) {
                continue;
            }
            if (chn >= min && chn <= max) {
                return esKey;
            }
        }
        // As default if nothing else is specified
        return null;
    }
}
