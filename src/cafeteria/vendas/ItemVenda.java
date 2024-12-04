package cafeteria.vendas;

// Classe que representa um item de venda em uma transação.
public class ItemVenda {
    private Long id; // Identificador único do item de venda
    private String nome; // Nome do produto
    private Integer medida; // Unidade de medida do produto
    private Integer quantidade; // Quantidade do produto vendido
    private Double preco; // Preço unitário do produto
    private Long vendaId; // Identificador da venda associada
    private Long idProduto; // Identificador do produto

    public ItemVenda() {}

    // Construtor com parâmetros.
    public ItemVenda(String nome, Integer medida, Integer quantidade, Double preco) {
        this.nome = nome;
        this.medida = medida;
        this.quantidade = quantidade;
        this.preco = preco;
    }

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

    public Integer getMedida() {
        return medida;
    }

    public void setMedida(Integer medida) {
        this.medida = medida;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Long getVendaId() {
        return vendaId;
    }

    public void setVendaId(Long vendaId) {
        this.vendaId = vendaId;
    }

    // Calcula o total do item de venda.
    public Double getTotal() {
        return preco * quantidade;
    }

    public Long getIdProduto() {
        return this.idProduto;
    }
}