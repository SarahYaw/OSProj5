/**
 * @author sarahyaw
 */
import java.util.*;
import java.io.*;
public class pager 
{
    public static void main(String[] args)
    {
        File file = new File("pages.dat");
        Scanner in=null;
        try
        {
            in = new Scanner(file);
        }
        catch(FileNotFoundException e)
        { 
            System.out.println("There is no file pages.dat nearby");
            System.exit(0);
        }
        do{
        String input="";
        String frameSize=in.next();
        while(in.hasNext())
        {
            String temp = in.next();
            if(temp.equals("-1"))
                break;
            else
                input+=temp+" ";
        }
        
        displayOutput(frameSize, input);

        }while(in.hasNext());
    }
    
    public static void displayOutput(String frameSize, String input)
    {
        int fifocount, lrucount, lfucount, optimalcount;

        System.out.print("FIFO:\n");
        fifocount = fifo(frameSize, input);
        System.out.print("\n");

        System.out.print("LRU:\n");
        lrucount = lru(frameSize, input);
        System.out.print("\n");

        System.out.print("LFU:\n");
        lfucount = lfu(frameSize, input);
        System.out.print("\n");

        System.out.print("Optimal:\n");
        optimalcount = optimal(frameSize, input);
        System.out.print("\n");

        System.out.print("\n");
        System.out.print("Using "+frameSize+" frames, the refrence string yielded:\n");
        System.out.print("Scheme\t#Faults\t%Optimal\n");
        System.out.print("FIFO\t"      +fifocount   +"\t"); System.out.printf("%.1f",((double)fifocount/optimalcount)*100);
        System.out.print("%\nLRU\t"    +lrucount    +"\t"); System.out.printf("%.1f",((double)lrucount/optimalcount)*100);
        System.out.print("%\nLFU\t"    +lfucount    +"\t"); System.out.printf("%.1f",((double)lfucount/optimalcount)*100);
        System.out.print("%\nOptimal\t"+optimalcount+"\t"); System.out.printf("%.1f",((double)optimalcount/optimalcount)*100);
        System.out.print("%\n\n");
    }

    //helper function to get columns courtesy of 
    //https://stackoverflow.com/questions/30426909/get-columns-from-two-dimensional-array-in-java
    public static String[] getCol(String[][] d, int ind, int framesize)
    {
        String[] col = new String[framesize];
        for(int i=0; i<col.length; i++)
            col[i] = d[i][ind];
        return col;
    }

    //first-in-first-out
    public static int fifo(String frameSize, String input)
    {
        ArrayList<String> lines = new ArrayList<String>();
        int count=0;
        int faults=0;
        String[] p = input.split(" ");
        page[] frames = new page[Integer.parseInt(frameSize)];
        String[][]display = new String[frames.length][(p.length*frames.length)];
        
        lines.add(input);
        lines.add("N");
        for(int a = 0; a<p.length; a++)
            lines.add("--");
        lines.add("N");

        for(int i = 0; i<p.length; i++)
        {
            for(int j = 0; j<frames.length; j++)
            {   
                boolean inalready = false;
                for(int k=0; k<frames.length; k++)
                    if (frames[k]!=null&&frames[k].name==Integer.parseInt(p[i]))
                        inalready=true;

                if(!inalready)
                {
                    int oldest=0;
                    for(int k=0; k<frames.length; k++)
                    {
                        if(frames[oldest]==null)
                            oldest=oldest;
                        else if(frames[k]==null||frames[k].counter>frames[oldest].counter)
                            oldest=k;
                    }
                    page temp = new page(Integer.parseInt(p[i]));
                    frames[oldest]= temp;
                    temp.counter=0;
                    faults++;
                }
                
                if(frames[j]!=null)
                    frames[j].counter+=1;
            }
            for(int a = 0; a<frames.length; a++)
            {
                for(int b = 0; b<frames.length; b+=frames.length)
                {
                    if(count<display[0].length)
                    {
                        if(frames[a]==null)
                            display[a][b+count]="0 ";
                        else
                            display[a][b+count]=frames[a].name+" ";
                    }
                }   
            }

            count+=Integer.parseInt(frameSize);
        }
        for(int a = 0; a<frames.length; a++)
        {
            for(int b = 0; b<(display[0].length); b+=frames.length)
                lines.add(display[a][b]);
            lines.add("N");
        }

        ArrayList<String> output = new ArrayList<String>();
        String line = "";
        for(int a=0; a<lines.size(); a++)
            if(!lines.get(a).equals("N"))
                line+=lines.get(a);
            else
            {
                line+="\n";
                output.add(line);
                line="";
            }

        output.add("\n");
        for(int b=0; b<output.size();b++)
        {
            if(output.get(b).length()<=78)
                System.out.print(output.get(b));
            else
            {
                System.out.println(output.get(b).substring(0,78));
                output.add(output.get(b).substring(78));
            }
        }

        return faults;
    }

    //least-recently used
    public static int lru(String frameSize, String input)
    {
        ArrayList<String> lines = new ArrayList<String>();
        int count=0;
        int faults=0;
        String[] p = input.split(" ");
        page[] frames = new page[Integer.parseInt(frameSize)];
        String[][]display = new String[frames.length][(p.length*frames.length)];
        
        lines.add(input);
        lines.add("N");
        for(int a = 0; a<p.length; a++)
            lines.add("--");
        lines.add("N");

        for(int i = 0; i<p.length; i++)
        {
            for(int j = 0; j<frames.length; j++)
            {   
                boolean inalready = false;
                for(int k=0; k<frames.length; k++)
                    if (frames[k]!=null&&frames[k].name==Integer.parseInt(p[i]))
                    {
                        inalready=true;
                        frames[k].lastUsed=i;
                    }

                if(!inalready)
                {
                    int replace=0;
                    for(int k=0; k<frames.length; k++)
                    {
                        if(frames[replace]==null)
                            replace=replace;
                        else if(frames[k]==null||frames[replace].lastUsed>frames[k].lastUsed)
                            replace=k;
                    }
                    page temp = new page(Integer.parseInt(p[i]));
                    frames[replace]= temp;
                    temp.lastUsed=i;
                    faults++;
                }
                
            }
            for(int a = 0; a<frames.length; a++)
            {
                for(int b = 0; b<frames.length; b+=frames.length)
                {
                    if(count<display[0].length)
                    {
                        if(frames[a]==null)
                            display[a][b+count]="0 ";
                        else
                            display[a][b+count]=frames[a].name+" ";
                    }
                }   
            }
            count+=Integer.parseInt(frameSize);

        }
        for(int a = 0; a<frames.length; a++)
        {
            for(int b = 0; b<(display[0].length); b+=frames.length)
                lines.add(display[a][b]);
            lines.add("N");
        }

        ArrayList<String> output = new ArrayList<String>();
        String line = "";
        for(int a=0; a<lines.size(); a++)
            if(!lines.get(a).equals("N"))
                line+=lines.get(a);
            else
            {
                line+="\n";
                output.add(line);
                line="";
            }

        output.add("\n");
        for(int b=0; b<output.size();b++)
        {
            if(output.get(b).length()<=78)
                System.out.print(output.get(b));
            else
            {
                System.out.println(output.get(b).substring(0,78));
                output.add(output.get(b).substring(78));
            }
        }
        return faults;
    }

    //least-frequently used (using a definition specified below)
    public static int lfu(String frameSize, String input)
    {
        ArrayList<String> lines = new ArrayList<String>();
        int count=0;
        int faults=0;
        String[] p = input.split(" ");
        page[] frames = new page[Integer.parseInt(frameSize)];
        String[][]display = new String[frames.length][(p.length*frames.length)];
        
        lines.add(input);
        lines.add("N");
        for(int a = 0; a<p.length; a++)
            lines.add("--");
        lines.add("N");

        for(int i = 0; i<p.length; i++)
        {
            for(int j = 0; j<frames.length; j++)
            {   
                boolean inalready = false;
                for(int k=0; k<frames.length; k++)
                {
                    if (frames[k]!=null&&frames[k].name==Integer.parseInt(p[i]))
                    {
                        inalready=true;
                        frames[k].calledUpon++;
                    }
                    if(frames[k]!=null)
                        frames[k].callsTotal++;
                }

                if(!inalready)
                {
                    int replace=0;
                    for(int k=0; k<frames.length; k++)
                    {
                        if(frames[replace]==null)
                            replace=replace;
                        else if(frames[k]==null|| ( ( (double)(frames[replace].calledUpon/frames.length)/(frames[replace].callsTotal/frames.length) )
                                                  > ( (double)(frames[k].calledUpon/frames.length)/(frames[k].callsTotal/frames.length) ) ))
                            replace=k;
                    }
                    page temp = new page(Integer.parseInt(p[i]));
                    frames[replace]= temp;
                    temp.calledUpon=1;
                    temp.callsTotal=1;
                    faults++;
                }
                
                if(frames[j]!=null)
                    frames[j].counter+=1;
            }
            for(int a = 0; a<frames.length; a++)
            {
                for(int b = 0; b<frames.length; b+=frames.length)
                {
                    if(count<display[0].length)
                    {
                        if(frames[a]==null)
                            display[a][b+count]="0 ";
                        else
                            display[a][b+count]=frames[a].name+" ";
                    }
                }   
            }
            count+=Integer.parseInt(frameSize);

        }
        for(int a = 0; a<frames.length; a++)
        {
            for(int b = 0; b<(display[0].length); b+=frames.length)
                lines.add(display[a][b]);
            lines.add("N");
        }

        ArrayList<String> output = new ArrayList<String>();
        String line = "";
        for(int a=0; a<lines.size(); a++)
            if(!lines.get(a).equals("N"))
                line+=lines.get(a);
            else
            {
                line+="\n";
                output.add(line);
                line="";
            }

        output.add("\n");
        for(int b=0; b<output.size();b++)
        {
            if(output.get(b).length()<=78)
                System.out.print(output.get(b));
            else
            {
                System.out.println(output.get(b).substring(0,78));
                output.add(output.get(b).substring(78));
            }
        }
        return faults;
    }

    //optimal   
    public static int optimal(String frameSize, String input)
    {
        ArrayList<String> lines = new ArrayList<String>();
        int count=0;
        int faults=0;
        String[] p = input.split(" ");
        page[] frames = new page[Integer.parseInt(frameSize)];
        String[][]display = new String[frames.length][(p.length*frames.length)];
        
        lines.add(input);
        lines.add("N");
        for(int a = 0; a<p.length; a++)
            lines.add("--");
        lines.add("N");

        for(int i = 0; i<p.length; i++)
        {
            for(int j = 0; j<frames.length; j++)
            {   
                boolean inalready = false;
                for(int k=0; k<frames.length; k++)
                    if (frames[k]!=null&&frames[k].name==Integer.parseInt(p[i]))
                    {
                        inalready=true;
                        int lowestind=99;
                        for(int a = p.length-1; a>i; a--)
                            if(Integer.parseInt(p[a])==frames[k].name)
                                lowestind=a;

                        frames[k].nextUsed=lowestind;
                        
                    }

                if(!inalready)
                {
                    int replace=0;
                    for(int k=0; k<frames.length; k++)
                    {
                        if(frames[replace]==null)
                            replace=replace;
                        else if(frames[k]==null||frames[replace].nextUsed<frames[k].nextUsed)
                            replace=k;
                        else if(frames[replace].nextUsed==frames[k].nextUsed)
                            replace=replace;
                    }
                    page temp = new page(Integer.parseInt(p[i]));
                    frames[replace]= temp;
                    int lwstin = 99;
                    for(int a = p.length-1; a>i; a--)
                       if(Integer.parseInt(p[a])==temp.name)
                            lwstin=a;
                    temp.nextUsed=lwstin;   
                    faults++;
                }
                
            }
            for(int a = 0; a<frames.length; a++)
            {
                for(int b = 0; b<frames.length; b+=frames.length)
                {
                    if(count<display[0].length)
                    {
                        if(frames[a]==null)
                            display[a][b+count]="0 ";
                        else
                            display[a][b+count]=frames[a].name+" ";//-"+frames[a].nextUsed+"\t";
                    }
                }   
            }
            count+=Integer.parseInt(frameSize);

        }
        for(int a = 0; a<frames.length; a++)
        {
            for(int b = 0; b<(display[0].length); b+=frames.length)
                lines.add(display[a][b]);
            lines.add("N");
        }

        ArrayList<String> output = new ArrayList<String>();
        String line = "";
        for(int a=0; a<lines.size(); a++)
            if(!lines.get(a).equals("N"))
                line+=lines.get(a);
            else
            {
                line+="\n";
                output.add(line);
                line="";
            }

        output.add("\n");
        for(int b=0; b<output.size();b++)
        {
            if(output.get(b).length()<=78)
                System.out.print(output.get(b));
            else
            {
                System.out.println(output.get(b).substring(0,78));
                output.add(output.get(b).substring(78));
            }
        }
        return faults;
    }

}
class page
{
    int name;
    int counter;//fifo
    int lastUsed;//lru
    int calledUpon, callsTotal;//lfu
    int nextUsed;//optimal
    public page(int n)
    {
        name=n;
    }
}
