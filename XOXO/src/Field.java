public class Field {
    public enum FieldStatus{
        X,O,EMPTY;
    }
    private double profitability;
    private FieldStatus status;
    int row;
    int col;

    Field(int row, int col){
        this.profitability = 0;
        this.row = row;
        this.col = col;
        this.status = FieldStatus.EMPTY;
    }

    Field(Field other){
        this.profitability = 0;
        this.row = other.row;
        this.col = other.col;
        this.status = other.status;
    }

    public FieldStatus getStatus(){
        return this.status;
    }

    public boolean isEmpty(){
        return this.status == FieldStatus.EMPTY;
    }

    public void setStatus(FieldStatus stat){
        status = stat;
    }

    public void setProfitability(double profitability) {
        this.profitability = profitability;
    }

    public double getProfitability(){
        return this.profitability;
    }

    public void resetProfitability(){
        this.profitability = 0;
    }
}
