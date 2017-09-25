package no.imr.stox.library;

import java.util.ArrayList;
import java.util.List;

/**
 * Meta function implements the IMetaFunction interface.
 *
 * @author aasmunds
 */
public class MetaFunction extends MetaReflObject implements IMetaFunction {

    private final List<IMetaParameter> metaParameters = new ArrayList<IMetaParameter>();
    private IMetaParameter metaOutput = null;
    private String dataStorage;
    private Boolean respondable = false; // Some of the functions are respondable
    private String tags = "";
    private String category = "";

    public MetaFunction(ILibrary library) {
        super(library);
    }

    public MetaFunction(ILibrary library, String clazz, String name, String description) {
        super(library, clazz, name, description);
    }

    @Override
    public List<IMetaParameter> getMetaParameters() {
        return metaParameters;
    }

    @Override
    public IMetaParameter getMetaOutput() {
        return metaOutput;
    }

    @Override
    public void setOutput(IMetaParameter output) {
        this.metaOutput = output;
    }

    @Override
    public IMetaParameter findMetaParameter(String name) {
        return (IMetaParameter) LibraryUtils.findMetaObject((List) metaParameters, name);
    }

    @Override
    public String getOutputDataTypeName() {
        if (getMetaOutput() == null) {
            return "N/A";
        }
        return getMetaOutput().getDataTypeName();
    }

    @Override
    public void setDataStorage(String dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public String getDataStorage() {
        return dataStorage;
    }

    @Override
    public Boolean isRespondable() {
        return respondable;
    }

    @Override
    public void setRespondable(Boolean respondable) {
        this.respondable = respondable;
    }

    @Override
    public String getTags() {
        return tags;
    }

    @Override
    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void setCategory(String category) {
        this.category = category;
    }

}
