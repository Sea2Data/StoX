package no.imr.stox.library;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Åsmund
 */
public final class LibraryUtils {

    /**
     * Hidden construtor
     */
    private LibraryUtils() {
    }

    /**
     *
     * @param metaObjects
     * @param name
     * @return find meta object from meta objects and name.
     */
    public static IMetaObject findMetaObject(List<IMetaObject> metaObjects, String name) {
        for (IMetaObject mf : metaObjects) {
            if (mf == null) {
                continue;
            }
            // check name + aliases:
            List<String> names = new ArrayList<>();
            names.add(mf.getName());
            if (mf.getAliases() != null) {
                names.addAll(mf.getAliases());
            }
            for (String n : names) {
                if (n == null || n.isEmpty()) {
                    continue;
                }
                if (n.equalsIgnoreCase(name)) {
                    return mf;
                }
            }
        }
        return null;
    }

}
