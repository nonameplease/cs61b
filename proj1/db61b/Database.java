package db61b;


import java.util.HashMap;


/** A collection of Tables, indexed by name.
 *  @author Scott Shao */
class Database {
    /** An empty database. */
    public Database() {
        dataBase = new HashMap<String, Table>();
    }

    /** Return the Table whose name is NAME stored in this database, or null
     *  if there is no such table. */
    public Table get(String name) {
        if (dataBase.containsKey(name)) {
            return dataBase.get(name);
        }
        return null;
    }

    /** Set or replace the table named NAME in THIS to TABLE.  TABLE and
     *  NAME must not be null, and NAME must be a valid name for a table. */
    public void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (dataBase.containsKey(name)) {
            dataBase.remove(name);
        }
        dataBase.put(name, table);
    }

    /** A set of Map with String and Table linked together.*/
    private HashMap<String, Table> dataBase;

}
