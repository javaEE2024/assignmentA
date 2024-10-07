package treepackage;

import java.util.Arrays;
import java.util.List;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;

public class FileDiff {

    public static Patch<String> diffFiles(String oldContent, String newContent) {
        List<String> original = Arrays.asList(oldContent.split("\\r?\\n"));
        List<String> revised = Arrays.asList(newContent.split("\\r?\\n"));
        return DiffUtils.diff(original, revised);
    }

    public static void compareFiles(String oldContent, String newContent) {
        Patch<String> patch = diffFiles(oldContent, newContent);

        for (AbstractDelta<String> delta : patch.getDeltas()) {
            DeltaType type = delta.getType();
            switch (type) {
                case INSERT:
                    System.out.println("新增行：");
                    delta.getTarget().getLines().forEach(line -> System.out.println("+ " + line));
                    break;
                case DELETE:
                    System.out.println("删除行：");
                    delta.getSource().getLines().forEach(line -> System.out.println("- " + line));
                    break;
                case CHANGE:
                    System.out.println("修改前的行：");
                    delta.getSource().getLines().forEach(line -> System.out.println("- " + line));
                    System.out.println("修改后的行：");
                    delta.getTarget().getLines().forEach(line -> System.out.println("+ " + line));
                    break;
                default:
                    break;
            }
            System.out.println();
        }
    }
}
