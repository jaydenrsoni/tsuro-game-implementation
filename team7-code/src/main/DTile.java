/**
 * Created by Michael on 4/17/2018.
 */
public class DTile extends ATile implements Cloneable {

    @Override
    public DTile clone() throws CloneNotSupportedException{
        DTile t = null;
        try{
            t = (DTile) super.clone();
        }catch(CloneNotSupportedException ignore){

        }
        return t;
    }
}
