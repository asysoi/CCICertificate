package cci.model.cert;

public interface ProductIterator {
    public Product first();
    public boolean hasNext();
    public Product next();
    public Product prev();
    public void reset();
    public void insertProductInNextPosition(Product product);
}
