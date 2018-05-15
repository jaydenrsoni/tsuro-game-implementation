/**
 * Created by Michael on 4/15/2018.
 */
public abstract class ATile implements Cloneable {

    double symmetry;

    double getSymmetry(){
        return symmetry;
    }

    public Object clone() throws CloneNotSupportedException {
        ATile t = null;
        try{
            t = (ATile) super.clone();
        }catch(CloneNotSupportedException ignore){

        }
        return t;
    }


}
