import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static Pod[] myPod = new Pod[2];
    public static Pod[] oppPod = new Pod[2];

    public static Checkpoint[] cps;

    public static int boostCount = 1;
    public static int firstTurns = 0;
    public static int shieldRecharge = 0;

    //Pod related
    public static long currentDestX;
    public static long currentDestY;
    public static String currentPower = "0";

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int laps = in.nextInt();
        int checkpointCount = in.nextInt();
        cps = new Checkpoint[checkpointCount];
        for (int i = 0; i < checkpointCount; i++) {
            int checkpointX = in.nextInt();
            int checkpointY = in.nextInt();
            cps[i] = new Checkpoint(new Position(checkpointX, checkpointY), i);
        }

        Checkpoint previous;
        Checkpoint current = cps[checkpointCount-1];
        Checkpoint next = cps[0];
        for (int i = 0; i < checkpointCount; i++) {
            previous = current;
            current = next;
            next = ((i == checkpointCount-1) ? cps[0] : cps[i+1]);
            current.calculateCheckpointAngle(previous, next);
        }

        while (true) {
            for (int i = 0; i < 2; i++) {
                int x = in.nextInt(); // x position of your pod
                int y = in.nextInt(); // y position of your pod
                int vx = in.nextInt(); // x speed of your pod
                int vy = in.nextInt(); // y speed of your pod
                int angle = in.nextInt(); // angle of your pod
                int nextCheckPointId = in.nextInt(); // next check point id of your pod
                myPod[i] = new Pod(x,y,vx,vy,angle,nextCheckPointId);
            }
            for (int i = 0; i < 2; i++) {
                int x2 = in.nextInt(); // x position of the opponent's pod
                int y2 = in.nextInt(); // y position of the opponent's pod
                int vx2 = in.nextInt(); // x speed of the opponent's pod
                int vy2 = in.nextInt(); // y speed of the opponent's pod
                int angle2 = in.nextInt(); // angle of the opponent's pod
                int nextCheckPointId2 = in.nextInt(); // next check point id of the opponent's pod
                oppPod[i] = new Pod(x2,y2,vx2,vy2,angle2,nextCheckPointId2);
            }

            for (int i = 0; i < 2; i++)
            {
                String power = "100";
                Pod pod = myPod[i];
                Checkpoint currentCP = cps[pod.nextCP];
                currentDestX = currentCP.pos.x;
                currentDestY = currentCP.pos.y;

                System.err.println("current angle " + pod.angle);
                double angle = calcThreePointsAngle(new Position(pod.x+1000*Math.cos(pod.angle*Math.PI/180),pod.y+1000*Math.sin(pod.angle*Math.PI/180)), new Position(pod.x, pod.y), currentCP.pos);
                System.err.println("real angle " + angle);
                if (Math.abs(angle) > 180)
                {
                    if (angle < 0)
                    {
                        angle = 360 + angle;
                    }
                    else
                    {
                        angle = angle - 360;
                    }
                }
                double nextCA = angle;
                double nextCD = calcTwoPointsDist(new Position(pod.x,pod.y), currentCP.pos);

                System.err.println("nextCA " + nextCA);
                System.err.println("nextCD " + nextCD);

                int currentIndex = currentCP.index;
                Checkpoint nextCP = ((currentIndex == checkpointCount-1) ? cps[0] : cps[currentIndex+1]);

                if (Math.abs(nextCA) <= 20 && nextCD < 2000)
                {
                    System.err.println("ADJUST");
                    adjustForNextCheckpoint(currentCP.angle, nextCP);
                    power = "100";
                }
                else if (nextCA > 90 || nextCA < -90)
                {
                    power = "0";
                }
                else if((nextCA > 25 || nextCA < -25) && nextCD < 1200)
                {
                    power = "0";
                }
                else
                {
                    power = "100";
                }

                if((firstTurns > 2) && Math.abs(nextCA)<2 && nextCD > 5000 && boostCount > 0)
                {
                    power = "BOOST";
                    boostCount--;
                }

                System.err.println(currentDestX + " " + currentDestY + " " + power);
                System.out.println(currentDestX + " " + currentDestY + " " + power);

                firstTurns++;
            }
        }
    }

    public static void adjustForNextCheckpoint(double angle, Checkpoint next)
    {
        if ( Math.abs(angle) <= 100 )
        {
            currentDestX = next.pos.x;
            currentDestY = next.pos.y;
        }
        return;
    }


    public static double calcThreePointsAngle(Position first, Position center, Position third)
    {
        return (Math.atan2(third.y - center.y, third.x - center.x) - Math.atan2(first.y - center.y, first.x - center.x)) * 180 / Math.PI;
    }

    public static double calcTwoPointsDist(Position first, Position second)
    {
        return Math.pow( Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2), 0.5 );
    }
}

class Pod
{
    public int x;
    public int y;
    public int vx;
    public int vy;
    public int angle;
    public int nextCP;

    public Pod(int x, int y, int vx, int vy, int angle, int nextCP)
    {
        this.x=x;
        this.y=y;
        this.vx=vx;
        this.vy=vy;
        this.angle=angle;
        this.nextCP=nextCP;
    }
}

class Checkpoint
{
    public Position pos;
    public int index;

    Double angle = null;

    public Checkpoint(Position pos, int index){
        this.pos = pos;
        this.index = index;
    }

    public Double calculateCheckpointAngle(Checkpoint previous, Checkpoint next)
    {
        double calc = Player.calcThreePointsAngle(previous.pos, pos, next.pos);
        System.err.println("real angle " + calc);
        if (Math.abs(calc) > 180)
        {
            if (calc < 0)
            {
                calc = 360 + calc;
            }
            else
            {
                calc = calc - 360;
            }
        }
        System.err.println("pos1 " + previous.pos);
        System.err.println("pos2 " + pos);
        System.err.println("pos3 " + next.pos);
        System.err.println("angle " + calc);

        return angle = calc;
    }
}

class Position
{
    long x;
    long y;

    public Position(long x, long y){
        this.x =x;
        this.y = y;
    }

    public Position(double x, double y){
        this.x = Math.round(x);
        this.y = Math.round(y);
    }

    public boolean equals(Object other)
    {
        return ((Position) other).x == x && ((Position) other).y == y;
    }

    public int hashCode()
    {
        return 13 * Long.hashCode(x) + 31 * Long.hashCode(y);
    }

    public String toString()
    {
        return "(" + x + "," + y + ")";
    }
}