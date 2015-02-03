package m10f.lipgen.parser.lr;

import java.util.List;

public class LRParseTableConflictException extends Exception {
    private List<LRParseTableConflict> conflicts;

    // TODO: add state -> items map for conflict resolution
    public LRParseTableConflictException(List<LRParseTableConflict> conflicts) {
        this.conflicts = conflicts;
    }

    public List<LRParseTableConflict> getConflicts() {
        return conflicts;
    }
}
