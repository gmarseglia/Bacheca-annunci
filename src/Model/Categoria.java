package Model;

public class Categoria {
    private String nome;
    private Categoria padre;

    public Categoria(String nome, Categoria padre) {
        this.nome = nome;
        this.padre = padre;
    }

    public Categoria(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getPadre() {
        return padre;
    }

    public void setPadre(Categoria padre) {
        this.padre = padre;
    }

    public String getID() {
        return this.nome;
    }
}
