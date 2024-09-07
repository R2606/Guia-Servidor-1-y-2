package raulospino.imc.servidor;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import raulospino.imc.vistas.VentanaPrincipal;

/**
 * Servidor TCP que maneja conexiones de clientes.
 */
public class ServidorTcp extends Thread {

    private volatile boolean estado;
    public static Map<String, SubProcesoCliente> listaDeClientes;
    private int puerto;
    private ServerSocket servicio;
    private VentanaPrincipal ventana;

    public ServidorTcp(Integer puerto, VentanaPrincipal v) {
        this.puerto = (puerto != null && puerto > 0) ? puerto : 9007;
        this.ventana = v;
        listaDeClientes = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        iniciarServicio();
    }

    /**
     * Inicializa el servicio de servidor y acepta conexiones de clientes.
     */
    public void iniciarServicio() {
        try {
            servicio = new ServerSocket(puerto);
            estado = true;
            actualizarUI(true);
            logAndAppend("Servidor disponible en el Puerto " + puerto);

            while (estado) {
                Socket cliente = servicio.accept();
                String ip = cliente.getInetAddress().getHostAddress();
                logAndAppend("Cliente " + ip + " conectado");

                SubProcesoCliente atencion = new SubProcesoCliente(cliente, ventana);
                listaDeClientes.put(ip, atencion);
                atencion.start();
            }
        } catch (IOException ex) {
            logAndAppend("ERROR al abrir el puerto " + puerto);
            actualizarUI(false);
        }
    }

    /**
     * Detiene el servicio del servidor y desconecta a todos los clientes.
     */
    public void detenerServicio() {
        if (estado) {
            estado = false;
            actualizarUI(false);

            // Desconectar a todos los clientes
            listaDeClientes.forEach((ip, cliente) -> {
                logAndAppend("Desconectando cliente " + ip);
             
            });

            // Limpiar la lista de clientes y cerrar el servidor
            listaDeClientes.clear();
            try {
                servicio.close();
            } catch (IOException ex) {
                logAndAppend("ERROR al cerrar el puerto " + puerto);
            }
        }
    }

    /**
     * Actualiza la interfaz de usuario según el estado del servidor.
     * @param enLinea Indica si el servidor está en línea o no.
     */
    private void actualizarUI(boolean enLinea) {
        ventana.getBtnIniciar().setText(enLinea ? "DETENER" : "INICIAR");
        ventana.getTxtEstado().setText(enLinea ? "ONLINE" : "OFF LINE");
        ventana.getTxtEstado().setForeground(enLinea ? Color.GREEN : Color.RED);
        ventana.getBtnIniciar().setForeground(enLinea ? Color.RED : Color.GREEN);
    }

    /**
     * Registra el mensaje y lo añade a la caja de log.
     * @param message El mensaje a registrar.
     */
    private void logAndAppend(String message) {
        String logMessage = log() + message;
        System.out.println(logMessage);
        ventana.getCajaLog().append(logMessage + "\n");
    }

    /**
     * Obtiene la cadena de formato para los logs.
     * @return Cadena de log formateada con fecha y hora actual.
     */
    private String log() {
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        return f.format(new Date()) + " - ";
    }
}
