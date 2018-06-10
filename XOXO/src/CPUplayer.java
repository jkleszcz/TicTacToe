import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class CPUplayer {

    GameField gameField;
    public ArrayList<Field> consideredFields = new ArrayList<>(100);
    static Semaphore sem = new Semaphore(0);

    CPUplayer(GameField gameField){
        this.gameField = gameField;
    }

    public Point drawX(){
        return this.findBestField();
    }

    public void addFieldsToConsider(int row, int col,ArrayList<Field> considered){
        this.removeFromConsideredFields(row,col,considered);
        for(int i=row-1 ; i<row+2 ; ++i){
            if(i<0 || i>9) {
                continue;
            }
            for(int j=col-1 ; j<col+2 ; ++j){
                if(j<0 || j>9) {
                    continue;
                }
                if(gameField.isEmpty(i,j) && !considered.contains(gameField.fields[i][j])) {
                    considered.add(gameField.fields[i][j]);
                }
            }
        }
    }

    private void removeFromConsideredFields(int row, int col,ArrayList<Field> considered){
        if(considered.contains(gameField.fields[row][col])){
            considered.remove(gameField.fields[row][col]);
        }
    }


    private Point findBestField(){
        double bestProfit = -1000;
        ArrayList<Field> bestFields = new ArrayList<>();
        Evaluating[] threads = new Evaluating[consideredFields.size()];
        for(int i=0 ; i<consideredFields.size() ; ++i){
            threads[i] = new Evaluating(consideredFields.get(i));
        }
        for(int i=0 ; i<threads.length ; ++i){
            threads[i].start();
        }
        sem.acquireUninterruptibly(threads.length);

        for(Field f :this.consideredFields) {
            if (f.getProfitability() > bestProfit) {
                bestFields.clear();
                bestProfit = f.getProfitability();
                bestFields.add(f);
            }else if(f.getProfitability() == bestProfit){
                bestFields.add(f);
            }
            f.resetProfitability();
        }
        Random gen = new Random();
        int randomP = gen.nextInt(bestFields.size());
        return new Point(bestFields.get(randomP).row,bestFields.get(randomP).col);
    }

    //////////////////////////Evaluating///////////////////////////
    class Evaluating extends Thread{

        private final Field[][] templateFields;
        private final Field tempEvaluatedField;
        private ArrayList<Field> tempConsideredFields = new ArrayList<>();
        Field evaluatedField;


        public Evaluating(Field evalField){
            this.evaluatedField = evalField;
            this.templateFields = new Field[10][];
            for(int i=0 ; i<10 ; ++i){
                this.templateFields[i] = new Field[10];
                for(int j=0 ; j<10 ; ++j){
                    this.templateFields[i][j] = new Field(gameField.fields[i][j]);
                }
            }
            this.tempEvaluatedField = this.templateFields[evaluatedField.row][evaluatedField.col];
            for(Field cF : consideredFields){
                this.tempConsideredFields.add(this.templateFields[cF.row][cF.col]);
            }
        }

        private void addPoint(int row, int col, Field.FieldStatus status){
            this.tempEvaluatedField.setStatus(Field.FieldStatus.X);
            addFieldsToConsider(tempEvaluatedField.row,tempEvaluatedField.col,tempConsideredFields);
        }

        private Point findNextMove(Field.FieldStatus status){
            double bestProfit = -1000;
            ArrayList<Field> bestField = new ArrayList<>();
            for(Field f:tempConsideredFields){
                f.setProfitability(winAbility(f.row,f.col,status));
            }
            for(Field f:tempConsideredFields){
                if(bestProfit < f.getProfitability()){
                    bestField.clear();
                    bestProfit = f.getProfitability();
                    bestField.add(f);
                }else if(bestProfit == f.getProfitability()){
                    bestField.add(f);
                }
                f.resetProfitability();
            }
            Random gen = new Random();
            Field bF = bestField.get(gen.nextInt(bestField.size()));
            return new Point(bF.row,bF.col);
        }



        public void run(){
            double profit = evaluateField(this.evaluatedField.row, this.evaluatedField.col, Field.FieldStatus.X);
            this.evaluatedField.setProfitability(profit);
            sem.release();
        }



        private double evaluateField(int row, int col, Field.FieldStatus status){
            double profit;
            profit = winAbility(row,col,status);

            Point opponentNextMove = findNextMove(Field.FieldStatus.O);
            if(opponentNextMove.x == row && opponentNextMove.y == col){
                profit += 0.6*winAbility(row,col, Field.FieldStatus.O);
            }else{
                profit -= 0.6*winAbility(row,col, Field.FieldStatus.O);
            }

            //To implement
//            this.addPoint(row, col, Field.FieldStatus.X);
//            Point opponentNextMove = findNextMove(Field.FieldStatus.O);
//            double opponentProfit = winAbility(opponentNextMove.x,opponentNextMove.y, Field.FieldStatus.O);
//            this.addPoint(opponentNextMove.x,opponentNextMove.y, Field.FieldStatus.O);
//            Point CPUnextMove = findNextMove(Field.FieldStatus.X);
//            double CPUNextProfit = winAbility(CPUnextMove.x,CPUnextMove.y, Field.FieldStatus.X);
            ///////////////



            return (profit/*+0.25*CPUNextProfit-0.5*opponentProfit*/);
        }


        //Count profit of move abilities and current win-state in this direction
        private double winAbility(int row, int col, Field.FieldStatus status){
            double totalPoints = 0;
            double abilityParam = 0.5;
            double ptLineParam = 10.0;
            if(veritcalWinAbility(row, col, status)){
                totalPoints += abilityParam;
                totalPoints += ptLineParam*Math.pow(verticalPointStatus(row, col, status),2);
            }
            if(horizontalWinAbility(row, col, status)){
                totalPoints += abilityParam;
                totalPoints += ptLineParam*Math.pow(horizontalPointStatus(row,col,status),2);
            }
            if(increasingDiagonalWinAbility(row, col, status)){
                totalPoints += abilityParam;
                totalPoints += ptLineParam*Math.pow(increasingDiagonalPointStatus(row,col,status),2);
            }
            if(decreasingDiagonalWinAbility(row, col, status)){
                totalPoints += abilityParam;
                totalPoints += ptLineParam*Math.pow(decreasingDiagonalPointStatus(row,col,status),2);
            }

            return totalPoints;
        }

        private boolean horizontalWinAbility(int row, int col, Field.FieldStatus status){
            int signsAbilitiesInOneLine = 0;
            final int nrow = this.templateFields[row][col].row;
            int ncol = this.templateFields[row][col].col-1;
            while(ncol >= 0 && (templateFields[nrow][ncol].getStatus() == Field.FieldStatus.EMPTY ||
            templateFields[nrow][ncol].getStatus() == status)){
                signsAbilitiesInOneLine++;
                ncol--;
            }
            ncol = this.templateFields[row][col].col+1;
            while(ncol <= 9 && (templateFields[nrow][ncol].getStatus() == Field.FieldStatus.EMPTY ||
                    templateFields[nrow][ncol].getStatus() == status)){
                signsAbilitiesInOneLine++;
                ncol++;
            }

            if(signsAbilitiesInOneLine>=4)
                return true;
            return false;
        }

        private double horizontalPointStatus(int row, int col, Field.FieldStatus status){
            int points = 0;
            final int nrow = this.templateFields[row][col].row;
            int ncol = this.templateFields[row][col].col-1;
            while(ncol >= 0 && (templateFields[nrow][ncol].getStatus() == status)){
                points += 1.0;
                ncol--;
            }
            ncol = this.templateFields[row][col].col+1;
            while(ncol <= 9 && (templateFields[nrow][ncol].getStatus() == status)){
                points += 1.0;
                ncol++;
            }
            return points;
        }

        private boolean veritcalWinAbility(int row, int col, Field.FieldStatus status){
            int signsAbilitiesInOneLine = 0;
            int nrow = this.templateFields[row][col].row-1;
            final int ncol = this.templateFields[row][col].col;
            while(nrow >= 0 && (templateFields[nrow][ncol].getStatus() == Field.FieldStatus.EMPTY ||
                    templateFields[nrow][ncol].getStatus() == status)){
                signsAbilitiesInOneLine++;
                nrow--;
            }
            nrow = this.templateFields[row][col].row+1;
            while(nrow <= 9 && (templateFields[nrow][ncol].getStatus() == Field.FieldStatus.EMPTY ||
                    templateFields[nrow][ncol].getStatus() == status)){
                signsAbilitiesInOneLine++;
                nrow++;
            }

            if(signsAbilitiesInOneLine>=4)
                return true;
            return false;
        }

        private double verticalPointStatus(int row, int col, Field.FieldStatus status){
            int points = 0;
            int nrow = this.templateFields[row][col].row-1;
            final int ncol = this.templateFields[row][col].col;
            while(nrow >= 0 && (templateFields[nrow][ncol].getStatus() == status)){
                points += 1.0;
                nrow--;
            }
            nrow = this.templateFields[row][col].row+1;
            while(nrow <= 9 && (templateFields[nrow][ncol].getStatus() == status)){
                points += 1.0;
                nrow++;
            }
            return points;
        }


        private boolean decreasingDiagonalWinAbility(int row,int col, Field.FieldStatus status){
            int signsAbilitiesInOneLine = 0;
            int nrow = this.templateFields[row][col].row-1;
            int ncol = this.templateFields[row][col].col-1;
            while(nrow >= 0 && ncol >= 0 && (templateFields[nrow][ncol].getStatus() == Field.FieldStatus.EMPTY ||
                    templateFields[nrow][ncol].getStatus() == status)){
                signsAbilitiesInOneLine++;
                nrow--;
                ncol--;
            }
            nrow = this.templateFields[row][col].row+1;
            ncol = this.templateFields[row][col].col+1;
            while(nrow <= 9 && ncol <= 9 && (templateFields[nrow][ncol].getStatus() == Field.FieldStatus.EMPTY ||
                    templateFields[nrow][ncol].getStatus() == status)){
                signsAbilitiesInOneLine++;
                nrow++;
                ncol++;
            }

            if(signsAbilitiesInOneLine>=4)
                return true;
            return false;
        }

        private double decreasingDiagonalPointStatus(int row, int col, Field.FieldStatus status){
            int points = 0;
            int nrow = this.templateFields[row][col].row-1;
            int ncol = this.templateFields[row][col].col-1;
            while(nrow >= 0 && ncol >= 0 && (templateFields[nrow][ncol].getStatus() == status)){
                points += 1.0;
                nrow--;
                ncol--;
            }
            nrow = this.templateFields[row][col].row+1;
            ncol = this.templateFields[row][col].col+1;
            while(nrow <= 9 && ncol <= 9 && (templateFields[nrow][ncol].getStatus() == status)) {
                points += 1.0;
                nrow++;
                ncol++;
            }
            return points;
        }

        private boolean increasingDiagonalWinAbility(int row, int col, Field.FieldStatus status){
            int signsAbilitiesInOneLine = 0;
            int nrow = this.templateFields[row][col].row+1;
            int ncol = this.templateFields[row][col].col-1;
            while(nrow <= 9 && ncol >= 0 && (templateFields[nrow][ncol].getStatus() == Field.FieldStatus.EMPTY ||
                    templateFields[nrow][ncol].getStatus() == status)){
                signsAbilitiesInOneLine++;
                nrow++;
                ncol--;
            }
            nrow = this.templateFields[row][col].row-1;
            ncol = this.templateFields[row][col].col+1;
            while(nrow >= 0 && ncol <= 9 && (templateFields[nrow][ncol].getStatus() == Field.FieldStatus.EMPTY ||
                    templateFields[nrow][ncol].getStatus() == status)){
                signsAbilitiesInOneLine++;
                nrow--;
                ncol++;
            }

            if(signsAbilitiesInOneLine>=4)
                return true;
            return false;
        }

        private double increasingDiagonalPointStatus(int row, int col, Field.FieldStatus status){
            int points = 0;
            int nrow = this.templateFields[row][col].row+1;
            int ncol = this.templateFields[row][col].col-1;
            while(nrow <= 9 && ncol >= 0 && (templateFields[nrow][ncol].getStatus() == status)){
                points +=1.0;
                nrow++;
                ncol--;
            }
            nrow = this.templateFields[row][col].row-1;
            ncol = this.templateFields[row][col].col+1;
            while(nrow >= 0 && ncol <= 9 && (templateFields[nrow][ncol].getStatus() == status)){
                points += 1.0;
                nrow--;
                ncol++;
            }
            return points;
        }
    }

















    ////////////////Only for tests////////////////////////
    private Point randomPoint(){
        Random generator = new Random();
        while(true) {
            Point result = new Point(generator.nextInt(10), generator.nextInt(10));
            if(gameField.isEmpty(result.x,result.y))
                return result;
        }
    }

    private Point getRandomFieldFromConsideredFields(){
        Random generator = new Random();
        Point result = new Point();
        int rand = generator.nextInt(consideredFields.size());
        Field randomField = this.consideredFields.get(rand);
        this.consideredFields.remove(randomField);
        result.setLocation(randomField.row, randomField.col);
        return result;
    }

    private void printConsideredPoints(){
        for(Field f : consideredFields){
            System.out.println(f.row+" "+f.col);
        }
    }
}
