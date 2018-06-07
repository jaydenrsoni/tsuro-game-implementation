import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 *
 * Represents token colors
 *
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

    //================================================================================
    // XML Helpers
    //================================================================================

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
