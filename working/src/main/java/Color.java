import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Created by vyasalwar on 4/27/18.
 */
public enum Color {
    BLUE,
    RED,
    GREEN,
    ORANGE,
    SIENNA,
    HOTPINK,
    DARKGREEN,
    PURPLE;

    public Element encodeColor(Document doc) {
        Element colorElement = doc.createElement("color");
        Text colorText = doc.createTextNode(this.name().toLowerCase());
        colorElement.appendChild(colorText);
        return colorElement;
    }

    public static Color decodeColor(Node colorNode) {
        return Color.valueOf(colorNode.getTextContent().toUpperCase());
    }
}
