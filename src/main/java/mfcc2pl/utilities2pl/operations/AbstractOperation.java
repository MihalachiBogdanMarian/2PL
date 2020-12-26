package mfcc2pl.utilities2pl.operations;

public class AbstractOperation implements Operation {

    protected String name;
    protected String tableName;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "AbstractOperation{" +
                "name='" + name + '\'' +
                ", table='" + tableName + '\'' +
                '}';
    }
}
