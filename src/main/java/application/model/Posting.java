package application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//doc id frequency a lists of position
public class Posting implements Serializable {
    int docId;
    int frequency;
    List<Integer> positions;
    public Posting(){}
    public Posting(int docId, int frequency, List<Integer> positions) {
        this.docId = docId;
        this.frequency = frequency;
        this.positions = positions;
    }

    public int getDocId() {
        return docId;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<Integer> getPositions() {
        return positions;
    }

}
