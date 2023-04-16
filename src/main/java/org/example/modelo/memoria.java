package org.example.modelo;

import java.util.ArrayList;
import java.util.List;

public class memoria {

    private enum TipoComando {
        ZERAR, NUMERO, DIV, MULT, SUB, SOMA, IGUAL, VIRGULA;

    }

    private static final memoria instancia = new memoria();

    private TipoComando ultimaOperacao = null;
    private Boolean substituir = false;
    private String textoAtual = "";
    private String textoBuffer = "";
    private final List<MemoriaObservador> observadores = new ArrayList<>();


    private memoria() {

    }

    public void adicionarObsevador(MemoriaObservador memoriaObservador) {
        observadores.add(memoriaObservador);
    }

    public void processarObservador(String valor) {

        TipoComando tipoComando = DetectarTipoComando(valor);
        if (tipoComando == null) {
            return;
        } else if (tipoComando == TipoComando.ZERAR) {
            textoAtual = "";
            textoBuffer = "";
            substituir = false;
            ultimaOperacao = null;
        } else if (tipoComando == TipoComando.NUMERO || tipoComando == TipoComando.VIRGULA) {
            textoAtual = substituir ? valor : textoAtual + valor;
            substituir = false;
        } else {
            substituir = true;
            textoAtual = obterResultadoOperacao();
            textoBuffer = textoAtual;
            ultimaOperacao = tipoComando;
        }


        observadores.forEach(o -> o.valorAlterado(getTextoAtual()));
    }

    private String obterResultadoOperacao() {
        if (ultimaOperacao == null || ultimaOperacao == TipoComando.IGUAL) {
            return textoAtual;
        }
        double numeroBuffer = Double.parseDouble(textoBuffer.replace(",", "."));
        double numeroAtual = Double.parseDouble(textoAtual.replace(",", "."));
        double resultado = 0;

        if (ultimaOperacao == TipoComando.SOMA) {
            resultado = numeroBuffer + numeroAtual;
        } else if (ultimaOperacao == TipoComando.SUB) {
            resultado = numeroBuffer - numeroAtual;
        } else if (ultimaOperacao == TipoComando.MULT) {
            resultado = numeroBuffer * numeroAtual;
        } else if (ultimaOperacao == TipoComando.DIV) {
            resultado = numeroBuffer / numeroAtual;
        }
        String resultadoString = Double.toString(resultado).replace(".", ",");
        boolean inteiro = resultadoString.endsWith(",0");
        return inteiro ? resultadoString.replace(",0", "") : resultadoString;
    }

    private TipoComando DetectarTipoComando(String valor) {
        if (textoAtual.isEmpty() && valor == "0") {
            return null;
        }

        try {
            Integer.parseInt(valor);
            return TipoComando.NUMERO;

        } catch (NumberFormatException e) {
            if ("AC".equals(valor)) {
                return TipoComando.ZERAR;
            } else if ("/".equals(valor)) {
                return TipoComando.DIV;
            } else if ("*".equals(valor)) {
                return TipoComando.MULT;
            } else if ("+".equals(valor)) {
                return TipoComando.SOMA;
            } else if ("-".equals(valor)) {
                return TipoComando.SUB;
            } else if ("=".equals(valor)) {
                return TipoComando.IGUAL;
            } else if (",".equals(valor) && !textoAtual.contains(",")) {
                return TipoComando.VIRGULA;
            }
        }
        return null;
    }

    public static memoria getInstancia() {
        return instancia;
    }

    public String getTextoAtual() {
        return textoAtual.isEmpty() ? "0" : textoAtual;
    }


}
