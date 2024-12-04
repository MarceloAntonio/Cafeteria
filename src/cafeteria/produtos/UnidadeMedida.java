package cafeteria.produtos;


// Enumeração que representa as unidades de medida disponíveis para os produtos.

public enum UnidadeMedida {

    UNIDADE("Unidade"),
    LATA("Lata"),
    LITRO("Litro"),
    PACOTE("Pacote"),
    FATIA("Fatia"),
    GARRAFA("Garrafa");

    private String descricao;

    // Construtor para a unidade de medida.
    UnidadeMedida(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    // Converte um valor ordinal para a respectiva unidade de medida.
    public static UnidadeMedida fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IllegalArgumentException("Valor inválido para UnidadeMedida: " + ordinal);
        }
        return values()[ordinal];
    }
}
