package REST.Beans;

import java.util.Random;

public class Coordinate {
    private int x;
    private int y;

    public Coordinate(){}

    public Coordinate(int x, int y){
        this.x=x;
        this.y=y;
    }

    public Coordinate(int[] district){
        switch (minDistrict(district)){
            case 1: this.x = generateRandomCoordinate(0,4);
                    this.y = generateRandomCoordinate(0,4);
                    break;
            case 2: this.x = generateRandomCoordinate(0,4);
                    this.y = generateRandomCoordinate(5,9);
                    break;
            case 3: this.x = generateRandomCoordinate(5,9);
                    this.y = generateRandomCoordinate(5,9);
                    break;
            case 4: this.x = generateRandomCoordinate(5,9);
                    this.y = generateRandomCoordinate(0,4);
                    break;
        }
    }

    public Coordinate(int district){
        switch (district){
            case 1: this.x = generateRandomCoordinate(0,4);
                this.y = generateRandomCoordinate(0,4);
                break;
            case 2: this.x = generateRandomCoordinate(0,4);
                this.y = generateRandomCoordinate(5,9);
                break;
            case 3: this.x = generateRandomCoordinate(5,9);
                this.y = generateRandomCoordinate(5,9);
                break;
            case 4: this.x = generateRandomCoordinate(5,9);
                this.y = generateRandomCoordinate(0,4);
                break;
        }
    }
    public int minDistrict(int[] district){
        int min = district[0];
        int id = 0;

        for(int i=0; i<district.length; i++)
            if( district[i] < min){
                min = district[i];
                id = i;
                break;
            }
        return id+1;
    }

    public int generateRandomCoordinate(int min, int max){
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int CoordinateToDistrict(){
        /*
        * (0,0) --- (0,4) | (0,5) --- (0,9)
        *        D1               D2
        * (4,0) --- (4,4) | (4,5) --- (4,9)
        * ---------------------------------
        * (5,0) --- (5,4) | (5,5) --- (5,9)
        *         D4              D3
        * (9,0) --- (9,4) | (9,5) --- (9,9)
        * */

        int d = -1;

        if (y<5)
            if (x<5)
                d=1;
            else
                d=4;
        else
            if (x<5)
                d=2;
            else
                d=3;

        return d;
    }
    @Override
    public String toString() {
        return "( " + x + "; " + y + " )";
    }
}
