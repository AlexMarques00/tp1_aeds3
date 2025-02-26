public class MyDate {

    // Atributos da Classe
    private int dia;
    private int mes;
    private int ano;

    // Construtor Padrão
    public MyDate() {
        this.dia = 1;
        this.mes = 1;
        this.ano = 2000;
    }

    // Construtor com Parâmetros
    public MyDate(int dia, int mes, int ano) {
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
    }

    // Getters
    public int getDia() {
        return dia;
    }

    public int getMes() {
        return mes;
    }

    public int getAno() {
        return ano;
    }

    // Setters
    public void setDia(int dia) {
        this.dia = dia;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", dia, mes, ano);
    }

    // Método para clonar a data
    @Override
    public MyDate clone() {
        return new MyDate(this.dia, this.mes, this.ano);
    }

}