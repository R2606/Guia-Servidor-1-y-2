/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package raulospino.seminario.imc.rmi.net;

import javax.print.attribute.standard.Severity;
import raulospino.seminario.imc.rmi.lib.IRemotaCalculoImc;
import javax.imageio.IIOException;
import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Server;

/**
 *
 * @author Raul
 */
public class Servidor {
    private int puerto = 9007;
    private CallHandler invocador;
    private Server servidor;
    private CalculoRmiImcImplem calculoImc;
    private IRemotaCalculoImc calculoImcRemoto;
    
    public Servidor(){
        invocador = new CallHandler();
        servidor = new Server();
        calculoImc = new CalculoRmiImcImplem();
    }
    
    public void iniciar() throws Exception {
        try {
            invocador.registerGlobal(IRemotaCalculoImc.class, calculoImc);
            servidor.bind(puerto, invocador);
        } catch (LipeRMIException e) {
            throw new Exception("Error: No es posible invocar metodo remotos");
        }catch(IIOException ex){
            throw new Exception("Error I/O");
        }
    }
    
    public void detener(){
        servidor.close();
    }
}

