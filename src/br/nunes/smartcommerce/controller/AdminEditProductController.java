package br.nunes.smartcommerce.controller;

import java.io.File;
import java.io.FileOutputStream;


import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Named;


import org.apache.catalina.core.ApplicationPart;

import br.nunes.smartcommerce.application.Session;
import br.nunes.smartcommerce.application.Util;
import br.nunes.smartcommerce.dao.ProdutoDAO;
import br.nunes.smartcommerce.model.Brand;
import br.nunes.smartcommerce.model.Produto;
import br.nunes.smartcommerce.model.User;

@Named("editProductController")
@ViewScoped
public class AdminEditProductController extends Controller<Produto> {


	private static final long serialVersionUID = 460310369802881573L;
	
	private User usuarioLogado;
	
	private ApplicationPart imagem;
	
	public AdminEditProductController() {
		super(new ProdutoDAO());
		Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		flash.keep("flashProduto");
		entity = (Produto) flash.get("flashProduct");	
		
	
		
	}

	@Override
	public Produto getEntity() {
		if(entity == null)
			entity = new Produto();
		return entity;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String encerrarSessao() {
		// encerrando a sessao
		Session.getInstance().invalidateSession();
		return "/index.xhtml?faces-redirect=true";
	}
	
	
	public User getUsuarioLogado() {
		if (usuarioLogado == null) // buscando o usuario da sessao
			usuarioLogado = (User) Session.getInstance().getAttribute("usuarioLogado");
		return usuarioLogado;
	}

	public void setUsuarioLogado(User usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}
	
	public String update() {
		if (validarDados()) {
			String caminhoImagem = "";
			if (imagem != null && imagem.getSubmittedFileName() != null) {
				caminhoImagem = "C:\\Users\\shika\\OneDrive\\Documentos\\Eclipse\\Workspace\\Dev-Server1\\SmartCommerce\\WebContent\\uploads\\" + imagem.getSubmittedFileName();
				

				try {
					// cria um espa�o de mem�ria que vai armazenar o conte�do da imagem PORQUE A IMAGEM � UM ARRAY DE BYTES
					byte[] bytesImagem = new byte[(int) imagem.getSize()];
					// l� o conteudo da imagem e joga dentro do array de bytes
					imagem.getInputStream().read(bytesImagem);
					// cria uma refer�ncia para o arquivo que ser� criado no lado do server
					File f = new File(caminhoImagem);
					// cria o objeto que ir� manipular o arquivo criado
					FileOutputStream fos = new FileOutputStream(f);
					// escreve o conte�do da imagem (upload) dentro do arquivo no servidor
					fos.write(bytesImagem);

					fos.close();
					
					getEntity().setImage(caminhoImagem);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (dao.update(getEntity())) {
				try {
					limpar();

					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Altera��o Realizada com Sucesso!"));

					FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

					Thread.sleep(1000);
					return "/admin_page/product_home.xhtml?faces-redirect=true";
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				Util.addInfoMessage("Erro ao Altera��o no banco de dados.");
				return "";
			}
		}
		return "";
	}

	public Brand[] getListaTipoProduto() {
		return Brand.values();
	}




	
	public ApplicationPart getImagem() {
		return imagem;
	}

	public void setImagem(ApplicationPart imagem) {
		this.imagem = imagem;
	}

	@Override
	public boolean validarDados() {
		if (getEntity().getName().isBlank()) {
			Util.addErrorMessage("O campo nome deve ser informado.");
			return false;
		}
	return true;
	}
	
	
}
