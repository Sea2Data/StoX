/**
 * Classes for converting hierarchical data (xml) to relational model.
 * 
 * In general these classes assume that the hierarchical model is mapped to java types by jaxb conventions, configured to extend HierarchichalData.HierarchicalData, and annotated with key information.
 * Reflection is used for access in several cases, so it is important that these assumptions are not violated.
 * Most importantly:
 * - getters are available for all elements named as get<nodename with capitalized first character>
 * - complexTypes are represented by a class with the same name as the complex type
 * - repeated elements on a complex type are represented by lists.
 * 
 * Conversion is schema dependent and designed to be generic, but format specific adaptations can be provided by implementing the interface ILeafNodeHandler.
 * 
 * For relating columns in tables to hierarchical model implement the interface ITableMakerNamingConventions.
 * 
 * The hard work is done by TableMaker and its subclasses. Additional documentation can be found in the javadoc for these classes.
 * For mapping to a set of keyed tables, use TableMaker 
 * For making a merged table of a type and all data from its ancestor types
 * 
 * For instance, for bioticv1_x:
 * converting MissionsType objects with TableMaker will make one table for each complex type. All tables will contain keys from all parent types.
 * converting AgedeterminationType objects with MergedTableMaker will make one table with all columns from AgedeterminationType, IndividualType, CatchsampleType, FishstationType and MissionType (the root element MissionsType contains no simple elements).
 * converting IndividualType objects with FlatTableMaker will make one table with all columns from IndividualType, AgedeterminationType and TagType, and key columns for: CatchsampleType, FishstationType and MissionType.
 * 
 * in all cases StringDescriptionType needs to be handled as leaf node.
 */
package HierarchicalData.RelationalConversion;
