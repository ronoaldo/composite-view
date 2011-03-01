package net.ronoaldo.tools.compositeview.tags;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Tag simples que realiza um include após realizar o processamento de seu
 * conteúdo.
 *
 * @author Ronoaldo Pereira &lt;ronoaldo@ronoaldo.net&gt;
 */
public class Extends extends SimpleTagSupport {

	/**
	 * Nome do template a ser utilizado para inclusão.
	 */
	private String template;

	/**
	 * Setter para que o atributo template funcione.
	 *
	 * @param template
	 *            o nome do template a ser extendido.
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * Implementação da Tag.
	 */
	@Override
	public void doTag() throws JspException, IOException {
		// Processa o body (definição de blocos)
		getJspBody().invoke(null);

		// Realiza o include do template
		try {
			PageContext pageContext = (PageContext) getJspContext();
			pageContext.include(template);
		} catch (ServletException e) {
			throw new JspException(e);
		}
	}

}