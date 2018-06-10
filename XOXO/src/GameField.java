public class GameField {

    Field[][] fields;
    CPUplayer player;
    boolean gameIsOver;

    GameField() {
        this.fields = new Field[10][];
        for (int i = 0; i < 10; ++i) {
            this.fields[i] = new Field[10];
            for (int j = 0; j < 10; ++j) {
                this.fields[i][j] = new Field(i,j);
            }
        }
        this.player = new CPUplayer(this);
        this.gameIsOver = false;
    }
    public boolean isEmpty(int row, int col){
        return this.fields[row][col].getStatus() == Field.FieldStatus.EMPTY;
    }

    public boolean gameOver(){
        if(verticalConditionOfGameOver() || horizontalConditionOfGameOver() ||
                decreasingDiagonalConditionGameOver() ||
                increasingDiagonalConditionGameOver()){
            this.gameIsOver = true;
            return true;
        }
        return false;
    }

    private boolean verticalConditionOfGameOver(){

        for(int i=0 ; i<10 ; ++i){
            Field.FieldStatus status = Field.FieldStatus.O;
            int signs = 0;
            for(int j=0 ; j<10 ; ++j){
                if(fields[j][i].getStatus() == Field.FieldStatus.EMPTY){
                    signs = 0;
                }else if(fields[j][i].getStatus() == status){
                    signs++;
                }else{
                    status = fields[j][i].getStatus();
                    signs = 1;
                }
                if(signs == 5){
                    System.out.println("Game over. Player "+status+" win!!! Vartical "+j+i);
                    return true;
                }
            }
        }
        return false;
    }
    private boolean horizontalConditionOfGameOver(){
        for(int i = 0 ; i<10 ; ++i){
            Field.FieldStatus status = Field.FieldStatus.O;
            int signs = 0;
            for(int j=0 ; j<10 ; ++j){
                if(fields[i][j].getStatus() == Field.FieldStatus.EMPTY){
                    signs = 0;
                }else if(fields[i][j].getStatus() == status){
                    signs++;
                }else{
                    status = fields[i][j].getStatus();
                    signs = 1;
                }
                if(signs == 5){
                    System.out.println("Game over. Player "+status+" win!!! Horizontal "+i+j);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean decreasingDiagonalConditionGameOver(){
        for(int i=9 ; i>=0 ; --i){
            Field.FieldStatus status = Field.FieldStatus.O;
            int signs = 0;
            int row = 0;
            int col = i;
            while(row<10 && col<10){
                if(fields[row][col].getStatus() == Field.FieldStatus.EMPTY){
                    signs = 0;
                }else if(fields[row][col].getStatus() == status){
                    signs++;
                }else{
                    status = fields[row][col].getStatus();
                    signs = 1;
                }
                if(signs == 5){
                    System.out.println("Game over. Player "+status+" win!!! Decreasing");
                    return true;
                }
                row++;
                col++;
            }
        }

        for(int i=0 ; i<10 ; ++i){
            Field.FieldStatus status = Field.FieldStatus.O;
            int signs = 0;
            int row = i;
            int col = 0;
            while(row<10 && col<10){
                if(fields[row][col].getStatus() == Field.FieldStatus.EMPTY){
                    signs = 0;
                }else if(fields[row][col].getStatus() == status){
                    signs++;
                }else{
                    status = fields[row][col].getStatus();
                    signs = 1;
                }
                if(signs == 5){
                    System.out.println("Game over. Player "+status+" win!!! Decreasing");
                    return true;
                }
                row++;
                col++;
            }
        }
        return false;
    }

    private boolean increasingDiagonalConditionGameOver(){
        for(int i=0 ; i<10 ; ++i){
            Field.FieldStatus status = Field.FieldStatus.O;
            int signs = 0;
            int row = i;
            int col = 0;
            while(row>=0 && col<10){
                if(fields[row][col].getStatus() == Field.FieldStatus.EMPTY){
                    signs = 0;
                }else if(fields[row][col].getStatus() == status){
                    signs++;
                }else{
                    status = fields[row][col].getStatus();
                    signs = 1;
                }
                if(signs == 5){
                    System.out.println("Game over. Player "+status+" win!!! Increasing");
                    return true;
                }
                row--;
                col++;
            }
        }
        for(int i=0 ; i<10 ; ++i){
            Field.FieldStatus status = Field.FieldStatus.O;
            int signs = 0;
            int row = 9;
            int col = i;
            while(row>=0 && col<10){
                if(fields[row][col].getStatus() == Field.FieldStatus.EMPTY){
                    signs = 0;
                }else if(fields[row][col].getStatus() == status){
                    signs++;
                }else{
                    status = fields[row][col].getStatus();
                    signs = 1;
                }
                if(signs == 5){
                    System.out.println("Game over. Player "+status+" win!!! Increasing");
                    return true;
                }
                row--;
                col++;
            }
        }
        return false;
    }

    public void changeFieldStatus(int row, int col, Field.FieldStatus status){
        this.fields[row][col].setStatus(status);
    }
}
