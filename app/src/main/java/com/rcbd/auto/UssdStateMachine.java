package com.rcbd.auto;


public class UssdStateMachine {


    public enum Estado {

        INICIO,

        ABRIR_USSD,

        MENU,

        ENVIAR_8,

        ENVIAR_2,

        ENVIAR_MB,

        ENVIAR_NUMERO,

        CONFIRMAR,

        CONCLUIDO,

        ERRO
    }



    private Estado estado = Estado.INICIO;



    public void iniciar(){

        estado = Estado.ABRIR_USSD;

    }



    public Estado getEstado(){

        return estado;

    }



    public void proximo(){

        switch(estado){


            case INICIO:

                estado = Estado.ABRIR_USSD;

                break;



            case ABRIR_USSD:

                estado = Estado.MENU;

                break;



            case MENU:

                estado = Estado.ENVIAR_8;

                break;



            case ENVIAR_8:

                estado = Estado.ENVIAR_2;

                break;



            case ENVIAR_2:

                estado = Estado.ENVIAR_MB;

                break;



            case ENVIAR_MB:

                estado = Estado.ENVIAR_NUMERO;

                break;



            case ENVIAR_NUMERO:

                estado = Estado.CONFIRMAR;

                break;



            case CONFIRMAR:

                estado = Estado.CONCLUIDO;

                break;



            default:

                estado = Estado.ERRO;

                break;

        }

    }



    public void erro(){

        estado = Estado.ERRO;

    }



    public boolean terminou(){

        return estado == Estado.CONCLUIDO;

    }


}
