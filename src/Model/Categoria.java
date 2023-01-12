package Model;

public class Categoria {
    private String nome;
    private String padre;

    public Categoria(String nome, String padre) {
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

    public String getPadre() {
        return padre;
    }

    public void setPadre(String padre) {
        this.padre = padre;
    }

    public String getID() {
        return this.nome;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "nome='" + nome + '\'' +
                ", padre='" + padre + '\'' +
                '}';
    }
}


