package net.ronoaldo.tools.compositeview.tags;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Tag simples que define um bloco de template.
 * 
 * @author Ronoaldo Pereira &lt;ronoaldo@ronoaldo.net&gt;
 */
public class Block extends SimpleTagSupport {

	/**
	 * Chave para recuperar o buffer, no escopo da requisição.
	 */
	private static final String BLOCK_REGISTRY_KEY = Block.class.getName()
			+ "-BLOCK_REGISTRY_KEY";

	/**
	 * {@link Logger} para depuração.
	 */
	protected Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	/**
	 * Nome do bloco.
	 */
	private String blockName;

	/**
	 * Setter para o atributo name, que identifica unicamente o bloco.
	 * 
	 * @param name
	 *            o nome do bloco.
	 */
	public void setName(String name) {
		this.blockName = name;
	}

	/**
	 * Realiza a implementação da Tag propriamente dita.
	 */
	@Override
	public void doTag() throws JspException, IOException {
		if (withinExtendsBlock()) {
			updateBlockContent();
		} else {
			renderBlock();
		}
	}

	/**
	 * Atualiza o conteúdo em cache do bloco, caso ele ainda não tenha sido
	 * definido.
	 * 
	 * @throws JspException
	 * @throws IOException
	 */
	private void updateBlockContent() throws JspException, IOException {
		// Renderiza o bloco caso ele ainda não exista
		if (getRegistry().get(blockName) != null)
			return;

		StringWriter sw = new StringWriter();
		JspFragment body = getJspBody();
		if (body != null) {
			body.invoke(sw);
		}
		getRegistry().put(blockName, sw.toString());
		logger.fine(String.format("Content for block %s updated to %s",
				blockName, sw.toString()));
	}

	/**
	 * Renderiza o bloco na página JSP.
	 * 
	 * @throws JspException
	 * @throws IOException
	 */
	private void renderBlock() throws JspException, IOException {
		// Insere o bloco na página
		updateBlockContent();
		JspWriter out = getJspContext().getOut();
		out.print(getRegistry().get(blockName));
	}

	/**
	 * Identifica se esta tag {@link Block} está dentro de uma tag
	 * {@link Extends}.
	 * 
	 * @return
	 */
	private boolean withinExtendsBlock() {
		return (getParent() instanceof Extends);
	}

	/**
	 * Recupera ou cria um registro no escopo da requisição, para armazenar os
	 * buffers dos blocos da página a ser exibida.
	 * 
	 * @return um {@link Map} contendo os valores associados ao nome do bloco.
	 */
	private Map<String, String> getRegistry() {
		JspContext ctx = getJspContext();

		@SuppressWarnings("unchecked")
		Map<String, String> registry = (Map<String, String>) ctx.getAttribute(
				Block.BLOCK_REGISTRY_KEY, PageContext.REQUEST_SCOPE);

		if (registry == null) {
			registry = new HashMap<String, String>();
			ctx.setAttribute(Block.BLOCK_REGISTRY_KEY, registry,
					PageContext.REQUEST_SCOPE);
		}

		return registry;
	}
}