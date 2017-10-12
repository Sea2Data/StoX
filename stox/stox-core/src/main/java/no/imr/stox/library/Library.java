package no.imr.stox.library;

import no.imr.sea2data.imrbase.matrix.IMetaMatrix;
import no.imr.sea2data.imrbase.matrix.MetaMatrix;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import no.imr.sea2data.imrbase.util.ImrSort;

/**
 * Process setup object for a beam process.. This class is responsible for
 * holding the function list and other relevant info for a process like process
 * file name and data storage name.
 *
 * @author aasmunds
 */
public final class Library implements ILibrary {

    // Strong references (owned objects):
    private final List<IMetaDataType> metaDataTypes = new ArrayList<>();
    private List<IMetaFunction> metaFunctions = new ArrayList<>();

    public Library() {
        // Load data types
        readFromResource("datatypes");
    }

    private static final String METAOBJECT_DATATYPE = "datatype";
    private static final String METAOBJECT_FUNCTION = "function";

    /**
     * add meta object
     *
     * @param mo
     */
    private void addMetaObject(IMetaObject mo) {
        if (mo instanceof IMetaFunction) {
            metaFunctions.add((IMetaFunction) mo);
        } else if (mo instanceof IMetaDataType) {
            metaDataTypes.add((IMetaDataType) mo);
        }
    }

    /**
     *
     * @param modelName
     * @param tags
     */
    @Override
    public void readFromResource(String modelName) {
        String resName = "setup/" + modelName + ".lib";
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resName);
        InputStreamReader isr = new InputStreamReader(in);
        IMetaObject mo = null;
        // Try-with-resources autocloses br
        // Note: br.close closes underlying streams (isr/in)
        try (BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("[")) {
                    String token = line.substring(1, line.length() - 1);
                    if (mo != null) {
                        addMetaObject(mo);
                        mo = null;
                    }
                    String metaObjectKey = token;
                    if (metaObjectKey.equalsIgnoreCase(METAOBJECT_DATATYPE)) {
                        mo = new MetaDataType(this);
                    } else if (metaObjectKey.equalsIgnoreCase(METAOBJECT_FUNCTION)) {
                        mo = new MetaFunction(this);
                        // Use model name as category for the function
                        ((IMetaFunction) mo).setCategory(modelName);
                    }
                } else if (mo != null) {
                    // Name: FilterBiotic
                    // Description: Filter biotic data
                    // Class: no.imr.sea2data.stox.functions.filter.FilterBiotic
                    if (line.startsWith("Name:")) {
                        mo.setName(line.substring("Name:".trim().length()).trim());
                        /*if (mo instanceof IMetaFunction) {
                         System.out.println("Reading function " + mo.getName());
                         }*/
                    } else if (line.startsWith("Alias:")) {
                        ((IMetaFunction) mo).getAliases().addAll(Arrays.asList(line.substring("Alias:".trim().length()).trim().split(";")));
                    } else if (line.startsWith("Description:")) {
                        mo.setDescription(line.substring("Description:".trim().length()).trim());
                    } else if (mo instanceof IMetaReflObject && line.startsWith("Class:")) {
                        ((IMetaReflObject) mo).setClazz(line.substring("Class:".trim().length()).trim());
                    } else if (line.startsWith("Respondable:")) {
                        ((IMetaFunction) mo).setRespondable(Boolean.valueOf(line.substring("Respondable:".trim().length()).trim()));
                    } else if (line.startsWith("Tags:")) {
                        ((IMetaFunction) mo).setTags(line.substring("Tags:".trim().length()));
                    } else if (mo instanceof IMetaFunction) {
                        if (line.startsWith("Parameter")) {
                            // Parameter1: DataType=FishStations, Name=Source
                            // Parameter2: DataType=Double, Name=DistInterval, Description=Distribution interval size in m
                            String[] tokens = line.split(":");
                            String[] tokens2 = tokens[1].split(",");
                            String name = null;
                            Boolean required = true;
                            Boolean deprecated = false;
                            IMetaDataType dataType = null;
                            IMetaMatrix metaMatrix = null;
                            String descr = null;
                            String defaultValue = null;
                            List<String> alias = new ArrayList<>();
                            List<String> values = null;
                            List<String> parentTags = null;
                            Boolean fileRef = false;
                            String tags = modelName;
                            for (String token : tokens2) {
                                String[] tokens3 = token.split("=");
                                if (tokens3.length != 2) {
                                    continue;
                                }
                                switch (tokens3[0].toLowerCase().trim()) {
                                    case "name":
                                        name = tokens3[1].trim();
                                        break;
                                    case "alias":
                                        alias.addAll(Arrays.asList(tokens3[1].trim().split(";")));
                                        break;
                                    case "values":
                                        values = new ArrayList<>();
                                        values.addAll(Arrays.asList(tokens3[1].trim().split(";")));
                                        break;
                                    case "parenttag":
                                        parentTags = new ArrayList<>();
                                        parentTags.addAll(Arrays.asList(tokens3[1].trim().split(";")));
                                        break;
                                    case "datatype":
                                        String dataTypeName = tokens3[1].trim();
                                        if (dataTypeName.startsWith(MATRIX_LITERAL) && dataTypeName.trim().length() > MATRIX_LITERAL.length()) {
                                            metaMatrix = new MetaMatrix(dataTypeName);
                                            dataTypeName = MATRIX_LITERAL;
                                        }
                                        dataType = this.findMetaDataType(dataTypeName);
                                        break;
                                    case "required":
                                        required = Boolean.valueOf(tokens3[1].trim());
                                        break;
                                    case "deprecated":
                                        deprecated = Boolean.valueOf(tokens3[1].trim());
                                        break;
                                    case "fileref":
                                        fileRef = Boolean.valueOf(tokens3[1].trim());
                                        break;
                                    case "description":
                                        descr = tokens3[1].trim();
                                        break;
                                    case "defaultvalue":
                                        defaultValue = tokens3[1].trim();
                                        break;
                                }
                            }
                            IMetaParameter mp = new MetaParameter(((IMetaFunction) mo), required, deprecated, fileRef, dataType, metaMatrix, name, descr, defaultValue, values, parentTags);
                            mp.getAliases().addAll(alias);
                            ((IMetaFunction) mo).getMetaParameters().add(mp);
                        } else if (line.startsWith("DataStorage:")) {
                            ((IMetaFunction) mo).setDataStorage(line.substring("DataStorage:".trim().length()).trim());
                        } else if (line.startsWith("Output:")) {
                            // Output: StationListForEstLayerAndStratum, IndividualDataStations
                            String[] tokens = line.split(":");
                            String[] tokens2 = tokens[1].split(",");
                            for (String dataTypeName : tokens2) {
                                dataTypeName = dataTypeName.trim();
                                IMetaMatrix metaMatrix = null;
                                if (dataTypeName.startsWith(MATRIX_LITERAL) && dataTypeName.trim().length() > MATRIX_LITERAL.length()) {
                                    metaMatrix = new MetaMatrix(dataTypeName);
                                    dataTypeName = MATRIX_LITERAL;
                                }
                                IMetaDataType dataType = this.findMetaDataType(dataTypeName);
                                if (dataType == null) {
                                    continue;
                                }
                                String descr = "Output:" + dataType.getDescription().trim();
                                String name = dataTypeName;
                                IMetaParameter outputParam = new MetaParameter(((IMetaFunction) mo), true, false, false, dataType, metaMatrix, name, descr, null, null, null);
                                ((IMetaFunction) mo).setOutput(outputParam);
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (mo != null) {
            addMetaObject(mo);
            mo = null;
        }
        if (metaFunctions != null) {
            // Sort the meta functions
            Collections.sort(metaFunctions, new ImrSort.TranslativeComparator(true));
        }
    }
    private static final String MATRIX_LITERAL = "Matrix";

    /**
     *
     * @return meta functions
     */
    @Override
    public List<IMetaFunction> getMetaFunctions() {
        return metaFunctions;
    }

    @Override
    public List<IMetaDataType> getMetaDataType() {
        return metaDataTypes;
    }

    @Override
    public IMetaFunction findMetaFunction(String name) {
        return (IMetaFunction) LibraryUtils.findMetaObject((List) metaFunctions, name);
    }

    @Override
    public IMetaDataType findMetaDataType(String name) {
        return (IMetaDataType) LibraryUtils.findMetaObject((List) metaDataTypes, name);
    }

    @Override
    public List getMetaFunctionsByCategory(String category) {
        return metaFunctions.stream().filter(mf -> mf.getCategory().equals(category)).collect(Collectors.toList());
    }

}
