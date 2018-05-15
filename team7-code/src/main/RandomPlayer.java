public class RandomPlayer extends MPlayer {

    RandomPlayer(String name){
        super(name);
    }

    public RandomPlayer clone() throws CloneNotSupportedException{
        RandomPlayer clonedPlayer = null;
        try{
            clonedPlayer = (RandomPlayer) super.clone();

        }catch(CloneNotSupportedException ignore){}
        clonedPlayer = new RandomPlayer(this.getName());
        clonedPlayer.color = Colors.valueOf(this.color.name());
        return clonedPlayer;
    }
}
