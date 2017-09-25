package no.imr.stox.library;

import java.util.ArrayList;
import java.util.List;
import no.imr.sea2data.imrbase.util.StrUtils;

/**
 * TODO: what is this?
 *
 * @author aasmunds
 */
public class MetaObject implements IMetaObject {

    private final ILibrary library;
    private String name;
    private String description = "";
    private final List<String> aliases = new ArrayList<>();
    boolean deprecated = false;

    public MetaObject(ILibrary library) {
        this.library = library;
    }

    public MetaObject(ILibrary library, String name, String description) {
        this.library = library;
        if(name.endsWith("@")) {
            name = StrUtils.stripEnd1(name);
            deprecated = true;
        }
        this.name = name;
        this.description = description;
    }

    @Override
    public ILibrary getLibrary() {
        return library;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;

    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }
}
