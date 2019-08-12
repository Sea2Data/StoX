package no.imr.stox.library;

import java.util.Arrays;
import no.imr.stox.util.matrix.IMetaMatrix;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO: what is this?
 *
 * @author aasmunds
 */
public class MetaParameter extends MetaObject implements IMetaParameter {

    private IMetaFunction metaFunction;
    private IMetaDataType metaDataType;
    private IMetaMatrix metaMatrix;
    private Boolean required;
    private Boolean deprecated;
    private Boolean fileRef = false;
    private Object defaultValue;
    private List<String> values;
    private List<Boolean> validValues;
    private List<String> valueAliases;
    private List<String> parentTags;

    public MetaParameter(ILibrary library) {
        super(library);
    }

    public MetaParameter(IMetaFunction metaFunction, Boolean required, Boolean deprecated, Boolean fileRef, IMetaDataType metaDataType, IMetaMatrix metaMatrix, String name, String description,
            Object defaultValue, List<String> values, List<String> parentTags) {
        super(metaFunction.getLibrary(), name, description);
        this.required = required;
        this.fileRef = fileRef;
        this.deprecated = deprecated;
        this.metaDataType = metaDataType;
        this.metaMatrix = metaMatrix;
        this.metaFunction = metaFunction;
        this.defaultValue = defaultValue;
        // Resolve the values aliases by ~split
        if (values != null && values.size() > 0) {
            valueAliases = values.stream().map(s -> {
                String elms[] = s.split("~", 2);
                return elms.length == 1 ? "" : elms[1];
            }).collect(Collectors.toList());
            validValues = values.stream().map(s -> {
                return !s.contains("@");
            }).collect(Collectors.toList());
            values = values.stream().map(s -> {
                String elms[] = s.split("~", 2);
                return elms[0].replaceAll("@", "");
            }).collect(Collectors.toList());
        }
        this.values = values;
        this.parentTags = parentTags;
    }

    @Override
    public String resolveParameterValue(String value) {
        if (getValues() != null && !getValues().isEmpty()) {
            int i = getValues().indexOf(value);
            if (i == -1) {
                // Try value aliases
                if (valueAliases != null && !valueAliases.isEmpty()) {
                    for (int j = 0; j < values.size(); j++) {
                        String s = valueAliases.get(j);
                        String elms[] = s.split("~");
                        Optional<String> opt = Arrays.stream(elms).filter(str -> str.equals(value)).findFirst();
                        if (opt.isPresent()) {
                            return values.get(j);
                        }
                    }
                }
                return null; // not found
            }
        }
        return value; // return value;
    }

    @Override
    public Boolean isRequired() {
        return required;
    }

    @Override
    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public Boolean isDeprecated() {
        return deprecated;
    }

    @Override
    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    @Override
    public Boolean isFileRef() {
        return fileRef;
    }

    @Override
    public void setFileRef(Boolean fileRef) {
        this.fileRef = fileRef;
    }

    @Override
    public IMetaDataType getMetaDataType() {
        return metaDataType;
    }

    @Override
    public void setMetaDataType(IMetaDataType metaDataType) {
        this.metaDataType = metaDataType;
    }

    @Override
    public IMetaFunction getMetaFunction() {
        return metaFunction;
    }

    @Override
    public IMetaMatrix getMetaMatrix() {
        return metaMatrix;
    }

    @Override
    public void setMetaMatrix(IMetaMatrix metaMatrix) {
        this.metaMatrix = metaMatrix;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getDataTypeName() {
        String s = getMetaDataType().getName();
        if (getMetaMatrix() != null) {
            s = getMetaMatrix().toString();
        }
        return s;
    }

    @Override
    public List<String> getValues() {
        return values;
    }

    @Override
    public List getValidValues() {
        return validValues;
    }

    @Override
    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public List<String> getParentTags() {
        return parentTags;
    }

    @Override
    public void setParentTags(List<String> parentTags) {
        this.parentTags = parentTags;
    }

    @Override
    public Boolean isParentParameter() {
        for (IMetaParameter mp : this.getMetaFunction().getMetaParameters()) {
            if (mp.equals(this)) {
                continue;
            }
            if (mp.getParentTags() != null) {
                for (String tag : mp.getParentTags()) {
                    String[] token = tag.split("\\.");
                    if (!(token.length == 2)) {
                        continue;
                    }
                    if (token[0].equalsIgnoreCase(getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Boolean isCompatible(IMetaParameter mp) {
        if (mp.getMetaMatrix() != null) {
            // Matrix data type check
            if (getMetaMatrix() == null) {
                return false;
            } else if (!mp.getMetaMatrix().toString().equals(getMetaMatrix().toString())) {
                return false;
            }
        }
        if (!getMetaDataType().equals(mp.getMetaDataType())) {
            return false;
        }
        return true;
    }
}
