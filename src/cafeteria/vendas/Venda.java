package cafeteria.vendas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
  Classe que representa uma venda no sistema.
  Uma venda contém informações sobre o cliente, itens vendidos, 
  desconto aplicado e a data e hora da transação.
 */
public class Venda {
    private Long id; // Identificador único da venda
    private LocalDateTime dataHorario; // Data e hora da venda
    private Long clienteId; // Identificador do cliente associado à venda
    private Double desconto; // Desconto aplicado à venda
    private List<ItemVenda> itens; // Lista de itens vendidos

    /**
      Construtor padrão da classe Venda.
      Inicializa a data e hora da venda com o momento atual,
      a lista de itens como uma nova ArrayList e o desconto como 0.0.
     */
    public Venda() {
        this.dataHorario = LocalDateTime.now();
        this.itens = new ArrayList<>();
        this.desconto = 0.0;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHorario;
    }

    public void setDataHora(LocalDateTime dataHorario) {
        this.dataHorario = dataHorario;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }

    
    // Calcula o total da venda, subtraindo o desconto do total dos itens.
    public Double getTotal() {
        double total = itens.stream()
            .mapToDouble(item -> item.getPreco() * item.getQuantidade())
            .sum();
        return total - desconto;
    }
}