package game.view;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
/**
 * This class is a zoomable ScrollPane with draggable content.
 * @author Luca Russ
 */
public class ZoomableScrollPane extends ScrollPane {

    /**
     * The Scale Value
     */
    private double scaleValue = 1;
	
    /**
     * The Intensity of the zoom
     */
    private double zoomIntensity = 0.001;
	
    /**
     * The Node displayed in the ScrollPane 
     */
    private Node target;
	
    /**
     * The Node which contains the target node
     */
    private Node zoomNode;
	
    /**
     * The maximum zoom Value
     */
    private double zoom_max = 2.5;
	
    /**
     * The Size of the Viewport
     */
    private double minViewPortSize = 900;
	
    /**
     * Creates a new ZoomableScrollPane.
     * Wraps the target Node in a Group and sets the modified Group as the content of the ScrollPane.
     * @param target the content of the ScrollPane
     */
    public ZoomableScrollPane(Node target) {
        super();
        this.target = target;
        this.zoomNode = new Group(target);
        setContent(outerNode(zoomNode));

        setPannable(true);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setFitToHeight(true); 
        setFitToWidth(true); 

        updateScale();
    }

    /**
     * This method adds an EventHandler to the Node after calling centeredNode(node).
     * @param node the unmodified node
     * @return outerNode the modified node
     */
    private Node outerNode(Node node) {
        Node outerNode = centeredNode(node);
        outerNode.setOnScroll(e -> {
            e.consume();
            onScroll(e.getDeltaY(), new Point2D(e.getX(), e.getY()));
        });
        return outerNode;
    }

    /**
     * This Method adds the Node node to a new VBox.
     * @param node to be set in the VBox
     * @return vBox which contains node
     */
    private Node centeredNode(Node node) {
        VBox vBox = new VBox(node);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    /**
     * Scales the Node target with the global scale value.
     */
    private void updateScale() {
        target.setScaleX(scaleValue);
        target.setScaleY(scaleValue);
    }

    /**
     * This method focuses the zoom to the mouse cursor
     * and handles the zoom value.
     * @param wheelDelta The scroll value.
     * @param mousePoint The current position of the mouse.
     */
    private void onScroll(double wheelDelta, Point2D mousePoint) {
        double zoomFactor = Math.exp(wheelDelta * zoomIntensity);

        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        // calculate pixel offsets from [0, 1] range
        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());
        scaleValue = scaleValue * zoomFactor;
        
        // calculate minimum zoom
        if(Math.max(target.getLayoutBounds().getWidth()*scaleValue, target.getLayoutBounds().getHeight()*scaleValue) < minViewPortSize) {

        	scaleValue = minViewPortSize / Math.max(target.getLayoutBounds().getWidth(), target.getLayoutBounds().getHeight());
            updateScale();
            this.layout();
            
        } else if(scaleValue > zoom_max) {
        	scaleValue = zoom_max;
        	updateScale();
            this.layout();
        }else {
        	updateScale();
            this.layout();

            // convert target coordinates to zoomTarget coordinates
            Point2D posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(mousePoint));

            // calculate adjustment of scroll position
            Point2D adjustment = target.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

            // convert back to [0, 1] range
            Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
            this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
            this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
        }
    }
}
