package cafeteria.clientes;


// Classe que representa um cliente no sistema.
public class Cliente {
    private Long id; // Identificador único do cliente
    private String nome; // Nome do cliente
    private String telefone; // Telefone do cliente

    // Construtor padrão
    public Cliente() {}

    // Construtor que inicializa nome e telefone
    public Cliente(String nome, String telefone) {
        this.nome = nome;
        this.telefone = telefone;
    }

    // Construtor que inicializa id, nome e telefone
    public Cliente(Long id, String nome, String telefone) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}