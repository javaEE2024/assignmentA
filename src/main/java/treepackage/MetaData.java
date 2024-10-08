package treepackage;

public record MetaData(String hash, String fileName, boolean isFile) {
    @Override
    public String toString() {
        return fileName + "   " + isFile + "   " + hash;
    }
}

