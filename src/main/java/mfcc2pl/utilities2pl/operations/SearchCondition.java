package mfcc2pl.utilities2pl.operations;

public class SearchCondition {

    private String fieldName;
    private String operator;
    private Object value;

    public SearchCondition() {
        this.fieldName = null;
        this.operator = null;
        this.value = null;
    }

    public SearchCondition(String fieldName, String operator, Object value) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String toString() {
        return "SearchCondition{" +
                "fieldName='" + fieldName + '\'' +
                ", operator='" + operator + '\'' +
                ", value=" + value +
                '}';
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
