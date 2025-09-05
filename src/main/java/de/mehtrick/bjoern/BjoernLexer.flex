package de.mehtrick.bjoern;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static de.mehtrick.bjoern.BjoernTypes.*;

%%

%{
  public BjoernLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class BjoernLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

FEATURE=Feature
BACKGROUND=Background
GIVEN=Given
WHEN=When
THEN=Then
SCENARIO=Scenario
SCENARIOS=Scenarios

IDENTIFIER=[a-zA-Z_][a-zA-Z_0-9]*
STRING_VALUE=\"([^\\\"]|\\.)*\"
COMMENT=#.*

%%
<YYINITIAL> {
  {WHITE_SPACE}      { return WHITE_SPACE; }

  "Feature:"         { return FEATURE; }
  "Background:"      { return BACKGROUND; }
  "Given:"           { return GIVEN; }
  "When:"            { return WHEN; }
  "Then:"            { return THEN; }
  "Scenario:"        { return SCENARIO; }
  "Scenarios:"       { return SCENARIOS; }
  
  "-"                { return DASH; }
  ":"                { return COLON; }
  
  {STRING_VALUE}     { return STRING; }
  {COMMENT}          { return COMMENT; }
  {IDENTIFIER}       { return IDENTIFIER; }

}

[^] { return BAD_CHARACTER; }