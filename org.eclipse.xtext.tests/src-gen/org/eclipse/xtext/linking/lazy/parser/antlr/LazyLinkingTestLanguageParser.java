/*
 * generated by Xtext
 */
package org.eclipse.xtext.linking.lazy.parser.antlr;

import com.google.inject.Inject;
import org.eclipse.xtext.linking.lazy.parser.antlr.internal.InternalLazyLinkingTestLanguageParser;
import org.eclipse.xtext.linking.lazy.services.LazyLinkingTestLanguageGrammarAccess;
import org.eclipse.xtext.parser.antlr.AbstractAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;

public class LazyLinkingTestLanguageParser extends AbstractAntlrParser {

	@Inject
	private LazyLinkingTestLanguageGrammarAccess grammarAccess;

	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	

	@Override
	protected InternalLazyLinkingTestLanguageParser createParser(XtextTokenStream stream) {
		return new InternalLazyLinkingTestLanguageParser(stream, getGrammarAccess());
	}

	@Override 
	protected String getDefaultRuleName() {
		return "Model";
	}

	public LazyLinkingTestLanguageGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(LazyLinkingTestLanguageGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
