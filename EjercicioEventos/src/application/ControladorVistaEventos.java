package application;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Estado;
import modelo.Evento;
import modelo.Municipio;
import modelo.TipoEvento;
import utilidades.GestorConexiones;

public class ControladorVistaEventos implements Initializable{
	private int codigoEventoSeleccionado;
	private GestorConexiones gestorConexiones;
	
	@FXML private ComboBox<TipoEvento> cboTipoEvento;
	@FXML private ComboBox<Estado> cboEstado;
	@FXML private ComboBox<Municipio> cboMunicipio;
	
	@FXML private TableView<Evento> tblViewEventos;
	@FXML private TableColumn<Evento,String> clmnNombreEvento;
	@FXML private TableColumn<Evento,Number> clmnCantidadInvitados;
	@FXML private TableColumn<Evento,Date> clmnFecha;
	@FXML private TableColumn<Evento,TipoEvento> clmnTipoEvento;
	@FXML private TableColumn<Evento,Estado> clmnEstado;
	@FXML private TableColumn<Evento,Municipio> clmnMunicipio;
	
	
	private ObservableList<TipoEvento> listaTipoEventos;
	private ObservableList<Estado> listaEstados;
	private ObservableList<Municipio> listaMunicipios;
	
	private ObservableList<Evento> listaEventos;
	
	@FXML private TextField txtNombreEvento;
	@FXML private TextArea txtADescripcion;
	@FXML private TextArea txtADireccion;
	@FXML private DatePicker dtPkrfecha;
	@FXML private TextField txtCantidadInvitados;
	
	@FXML private Button bntGuardar;
	@FXML private Button bntEliminar;
	@FXML private Button bntNuevo;
	@FXML private Button bntActualizar;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		asociarColumnas();
		gestorConexiones = new GestorConexiones();
		gestorConexiones.establecerConexion();
		//Inicializar ObservableList
		listaTipoEventos = FXCollections.observableArrayList();
		listaEstados = FXCollections.observableArrayList();
		listaMunicipios = FXCollections.observableArrayList();
		listaEventos = FXCollections.observableArrayList();
		cboTipoEvento.setItems(listaTipoEventos);
		cboEstado.setItems(listaEstados);
		cboMunicipio.setItems(listaMunicipios);
		
		//Enlazar TableView con el ObservableList
		tblViewEventos.setItems(listaEventos);
		
		tblViewEventos.getSelectionModel().selectedItemProperty().addListener(
				new  ChangeListener<Evento>() {

					@Override
					public void changed(
							ObservableValue<? extends Evento> arg0,
							Evento valorAnterior, 
							Evento valorNuevo) {
						if (valorNuevo!=null){
							codigoEventoSeleccionado = valorNuevo.getCodigoEvento();
							txtNombreEvento.setText(valorNuevo.getNombreEvento());
							txtADescripcion.setText(valorNuevo.getDescripcion());
							txtADireccion.setText(valorNuevo.getDireccion());
							txtCantidadInvitados.setText(String.valueOf(valorNuevo.getCantidadInvitados()));
							dtPkrfecha.setValue(valorNuevo.getFecha().toLocalDate());
							cboTipoEvento.getSelectionModel().select(valorNuevo.getTipoEvento());
							cboEstado.getSelectionModel().select(valorNuevo.getEstado());
							cboMunicipio.getSelectionModel().select(valorNuevo.getMunicipio());
							
							bntGuardar.setDisable(true);
							bntEliminar.setDisable(false);
							bntActualizar.setDisable(false);
						}
						
					}
						
				}
		);
		
		llenarInformacion();
		
		gestorConexiones.cerrarConexion();
	}
	
	@FXML
	public void limpiar(){
		txtNombreEvento.setText(null);
		txtADescripcion.setText(null);
		txtADireccion.setText(null);
		dtPkrfecha.setValue(null);
		txtCantidadInvitados.setText(null);
		cboEstado.getSelectionModel().select(null);
		cboMunicipio.getSelectionModel().select(null);
		cboTipoEvento.getSelectionModel().select(null);
		tblViewEventos.getSelectionModel().select(null);
		
		bntGuardar.setDisable(false);
		bntEliminar.setDisable(true);
		bntActualizar.setDisable(true);
	} 
	
	public void asociarColumnas(){
		clmnNombreEvento.setCellValueFactory(new PropertyValueFactory<Evento,String>("nombreEvento"));
		clmnCantidadInvitados.setCellValueFactory(new PropertyValueFactory<Evento,Number>("cantidadInvitados"));
		clmnFecha.setCellValueFactory(new PropertyValueFactory<Evento,Date>("fecha"));
		clmnTipoEvento.setCellValueFactory(new PropertyValueFactory<Evento,TipoEvento>("tipoEvento"));
		clmnEstado.setCellValueFactory(new PropertyValueFactory<Evento,Estado>("estado"));
		clmnMunicipio.setCellValueFactory(new PropertyValueFactory<Evento,Municipio>("municipio"));
	}
	
	public void llenarInformacion(){
		TipoEvento.llenarComboBoxTipoEvento(
				gestorConexiones.getConexion(),
				listaTipoEventos
		);
		Estado.llenarComboBoxEstado(
				gestorConexiones.getConexion(), 
				listaEstados
		);		
		Municipio.llenarComboBoxMunicipio(
				gestorConexiones.getConexion(),
				listaMunicipios
		);
		Evento.llenarListaEventos(
				gestorConexiones.getConexion(), 
				listaEventos
		);
	}
	
	@FXML
	public void guardarRegistro(){
		ArrayList<String> errores = validarCampos();
		if (errores.size()>0){
			String strErrores="";
			for (int i = 0; i<errores.size();i++)
				strErrores += errores.get(i) + "\n";
			mostrarMensaje(AlertType.ERROR, "Error", "Error de validacion", strErrores);
			return;
		}
		Evento e = 
				new Evento(
						0,
						txtNombreEvento.getText(),
						txtADescripcion.getText(),
						txtADireccion.getText(),
						Date.valueOf(dtPkrfecha.getValue()),
						Integer.valueOf(txtCantidadInvitados.getText()),
						cboTipoEvento.getSelectionModel().getSelectedItem(),
						cboEstado.getSelectionModel().getSelectedItem(),
						cboMunicipio.getSelectionModel().getSelectedItem()
				);
		gestorConexiones.establecerConexion();
		int resultado = e.guardarRegistro(gestorConexiones.getConexion());
		gestorConexiones.cerrarConexion();
		if (resultado <=0){
			Alert mensaje = new Alert(AlertType.ERROR);
			mensaje.setHeaderText("Error al guardar");
			mensaje.setContentText("No se pudo almacenar el registro");
			mensaje.setTitle("Error");
			mensaje.show();
		} else {
			listaEventos.add(e);
			Alert mensaje = new Alert(AlertType.INFORMATION);
			mensaje.setHeaderText("Exito");
			mensaje.setContentText("Registro almacenado correctamente");
			mensaje.setTitle("Exito");
			mensaje.show();
		}
	}
	
	@FXML
	public void actualizarRegistro(){
		ArrayList<String> errores = validarCampos();
		if (errores.size()>0){
			String strErrores="";
			for (int i = 0; i<errores.size();i++)
				strErrores += errores.get(i) + "\n";
			mostrarMensaje(AlertType.ERROR, "Error", "Error de validacion", strErrores);
			return;
		}
		System.out.println("Evento seleccionado:"+codigoEventoSeleccionado);
		Evento e = 
				new Evento(
						//Puede utilizar en vez de codigoEventoSeleccionado esta linea: tblViewEventos.getSelectionModel().getSelectedItem().getCodigoEvento()
						codigoEventoSeleccionado,
						txtNombreEvento.getText(),
						txtADescripcion.getText(),
						txtADireccion.getText(),
						Date.valueOf(dtPkrfecha.getValue()),
						Integer.valueOf(txtCantidadInvitados.getText()),
						cboTipoEvento.getSelectionModel().getSelectedItem(),
						cboEstado.getSelectionModel().getSelectedItem(),
						cboMunicipio.getSelectionModel().getSelectedItem()
				);
		gestorConexiones.establecerConexion();
		int resultado = e.actualizarRegistro(gestorConexiones.getConexion());
		gestorConexiones.cerrarConexion();
		if (resultado <=0){
			Alert mensaje = new Alert(AlertType.ERROR);
			mensaje.setHeaderText("Error al actualizar");
			mensaje.setContentText("No se actualizo el registro");
			mensaje.setTitle("Error");
			mensaje.show();
		} else {
			listaEventos.set(tblViewEventos.getSelectionModel().getSelectedIndex(),e);
			Alert mensaje = new Alert(AlertType.INFORMATION);
			mensaje.setHeaderText("Exito");
			mensaje.setContentText("Registro actualizado correctamente");
			mensaje.setTitle("Exito");
			mensaje.show();
		}
	}
	
	@FXML
	public void eliminarRegistro(){
		if (tblViewEventos.getSelectionModel().getSelectedItem() == null){
			mostrarMensaje(AlertType.ERROR, "Error", "Error al eliminar registro", "No se ha seleccionado ningun registro");
			return;
		}
		
		gestorConexiones.establecerConexion();
		int resultado = tblViewEventos.getSelectionModel().getSelectedItem().eliminarRegistro(gestorConexiones.getConexion());
		gestorConexiones.cerrarConexion();
		if (resultado <=0){
			mostrarMensaje(AlertType.ERROR, "Error", "Error al eliminar registro", "No se elimino el registro");
		} else {
			listaEventos.remove(tblViewEventos.getSelectionModel().getSelectedIndex());
			mostrarMensaje(AlertType.INFORMATION, "Exito", "Exito", "Registro eliminado correctamente");
		}
	}
	
	public void mostrarMensaje(AlertType tipoMensaje, String titulo, String encabezado, String contenido){
		Alert mensaje = new Alert(tipoMensaje);
		mensaje.setHeaderText(encabezado);
		mensaje.setContentText(contenido);
		mensaje.setTitle(titulo);
		mensaje.show();
	}
	
	public ArrayList<String> validarCampos(){
		ArrayList<String> errores = new ArrayList<String>();
		if (txtNombreEvento.getText().trim().equals(""))
			errores.add("El campo nombre evento esta vacio");
		if (txtADescripcion.getText().trim().equals(""))
			errores.add("El campo descripcion esta vacio");
		if (txtADireccion.getText().trim().equals(""))
			errores.add("El campo direccion esta vacio");
		if (txtCantidadInvitados.getText().trim().equals(""))
			errores.add("El campo cantidad invitados esta vacio");
		if (dtPkrfecha.getValue() == null)
			errores.add("El campo fecha esta vacio");
		if (cboEstado.getSelectionModel().getSelectedItem() == null)
			errores.add("El campo estado esta vacio");
		if (cboMunicipio.getSelectionModel().getSelectedItem() == null)
			errores.add("El campo municipio esta vacio");
		if (cboTipoEvento.getSelectionModel().getSelectedItem() == null)
			errores.add("El campo tipo evento esta vacio");
		
		try {
			Integer.valueOf(txtCantidadInvitados.getText());
		}catch(NumberFormatException e){
			//e.printStackTrace();
			errores.add("El campo cantidad invitados no es numerico");
		}
		
		if (txtNombreEvento.getText().length()>500)
			errores.add("Nombre evento no puede superar los 500 caracteres");
		//...
		
		//En caso de necesitar validaciones con expresiones regulares
		//utilizar el siguiente codigo
		/*String patron = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,3})$";//"[0-9]{4}-[0-9]{4}-[0-9]{5}";
		Pattern pattern = Pattern.compile(patron);
		Matcher matcher = pattern.matcher(txtNombreEvento.getText());
		if (!matcher.matches())
			errores.add("No cumple con el patron de la identidad");
		 */
		
		return errores;
	}
}
