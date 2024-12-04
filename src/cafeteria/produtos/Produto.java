package cafeteria.produtos;


// Classe que representa um produto no sistema.

public class Produto {
    private Long id; // Identificador único do produto
    private String nome; // Nome do produto
    private UnidadeMedida medida; // Unidade de medida do produto
    private Double preco; // Preço do produto
    private Integer estoque; // Estoque total do produto
    private Integer quantidadeDisponivel; // Quantidade disponível para venda

    // Construtor padrão
    public Produto() {}

    
    // Construtor que inicializa nome, medida, preço e estoque do produto.
    public Produto(String nome, UnidadeMedida medida, Double preco, Integer estoque) {
        this.nome = nome;
        this.medida = medida;
        this.preco = preco;
        this.estoque = estoque;
    }

    
    // Construtor que inicializa id, nome, medida, preço e estoque do produto.
    public Produto(Long id, String nome, UnidadeMedida medida, Double preco, Integer estoque) {
        this.id = id;
        this.nome = nome;
        this.medida = medida;
        this.preco = preco;
        this.estoque = estoque;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UnidadeMedida getMedida() {
        return medida;
    }

    public void setMedida(UnidadeMedida medida) {
        this.medida = medida;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public Integer getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }
}