package no.imr.stox.library;

/**
 * Get MetaObject with class name for instantiate by reflection
 *
 * @author aasmunds
 */
public class MetaReflObject extends MetaObject implements IMetaReflObject {

    private String clazz;

    public MetaReflObject(ILibrary library) {
        super(library);
    }

    public MetaReflObject(ILibrary library, String clazz, String name, String description) {
        super(library, name, description);
        this.clazz = clazz;
    }

    @Override
    public String getClazz() {
        return clazz;
    }

    @Override
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}
