/**
 * PTStemmer - Java Stemming toolkit for the Portuguese language (C) 2008 Pedro
 * Oliveira
 *
 * This file is part of PTStemmer. PTStemmer is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * PTStemmer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PTStemmer. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.itera.preprocess.stempt;

import com.itera.structures.ArrayListSerializable;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

/**
 * Orengo Stemmer as defined in:<br>
 * V. Orengo and C. Huyck, "A stemming algorithm for the portuguese language,"
 * String Processing and Information Retrieval, 2001. SPIRE 2001.
 * Proceedings.Eighth International Symposium on, 2001, pp. 186-193.<br>
 * Augmented using the rules present in:<br>
 * http://www.inf.ufrgs.br/%7Earcoelho/rslp/integrando_rslp.html
 *
 * @author Pedro Oliveira
 *
 */
public class OrengoStemmer extends Stemmer implements Serializable {

    private class Rule implements Comparable<Rule>, Serializable {

        private String suffix;
        private int size;
        private String replacement;
        private List<String> exceptions;

        public Rule(String suffix, int size, String replacement, String[] exceptions) {
            this.suffix = suffix;
            this.size = size;
            this.replacement = replacement;
            this.exceptions = Arrays.asList(exceptions);
        }

        public String getSuffix() {
            return suffix;
        }

        public int getSize() {
            return size;
        }

        public String getReplacement() {
            return replacement;
        }

        public List<String> getExceptions() {
            return exceptions;
        }

        //@Override
        public int compareTo(Rule o) {
            return this.suffix.compareTo(o.getSuffix());
        }
    }

    public OrengoStemmer() {
        pluralreductionrules = new ArrayListSerializable<>();
        femininereductionrules = new ArrayListSerializable<>();
        adverbreductionrules = new ArrayListSerializable<>();
        augmentativediminutivereductionrules = new ArrayListSerializable<>();
        nounreductionrules = new ArrayListSerializable<>();
        verbreductionrules = new ArrayListSerializable<>();
        vowelremovalrules = new ArrayListSerializable<>();
        fillPlural();
        fillFeminine();
        fillAugmentativeDiminutive();
        fillAdverb();
        fillNoun();
        fillVerb();
        fillVowel();
    }

    @Override
    protected String stemming(String word) {
        return algorithm(word);
    }

    private String algorithm(String st) {
        String stem = st;
        String aux;
        if (stem.endsWith("s")) {
            stem = applyRules(stem, pluralreductionrules);
        }
        if (stem.endsWith("a")) {
            stem = applyRules(stem, femininereductionrules);
        }
        stem = applyRules(stem, augmentativediminutivereductionrules);
        stem = applyRules(stem, adverbreductionrules);
        aux = stem;
        stem = applyRules(stem, nounreductionrules);
        if (aux.compareTo(stem) == 0) {
            aux = stem;
            stem = applyRules(stem, verbreductionrules);
            if (aux.compareTo(stem) == 0) {
                stem = applyRules(stem, vowelremovalrules);
            }
        }
        stem = removeDiacritics(stem);
        return stem;
    }

    private String applyRules(String st, ArrayListSerializable<Rule> rules) {
        for (Rule r : rules) {
            if (r.getExceptions().contains(st)) {
                break;
            }
            if (st.endsWith(r.getSuffix())) {
                if (st.length() >= r.getSuffix().length() + r.getSize()) {
                    return st.substring(0, st.length() - r.getSuffix().length()) + r.getReplacement();
                }
            }
        }
        return st;
    }

    private String removeDiacritics(String st) {
        String res = st;
        res = Normalizer.normalize(res, Normalizer.Form.NFD);
        res = res.replaceAll("[^\\p{ASCII}]", "");
        return res;
    }

    private final ArrayListSerializable<Rule> pluralreductionrules;
    private final ArrayListSerializable<Rule> femininereductionrules;
    private final ArrayListSerializable<Rule> adverbreductionrules;
    private final ArrayListSerializable<Rule> augmentativediminutivereductionrules;
    private final ArrayListSerializable<Rule> nounreductionrules;
    private final ArrayListSerializable<Rule> verbreductionrules;
    private final ArrayListSerializable<Rule> vowelremovalrules;

    private void fillPlural() {
        pluralreductionrules.add(new Rule("�es", 3, "�o", new String[]{}));
        pluralreductionrules.add(new Rule("�es", 1, "�o", new String[]{"m�e"}));
        pluralreductionrules.add(new Rule("ais", 1, "al", new String[]{"cais", "mais"}));
        pluralreductionrules.add(new Rule("�is", 2, "el", new String[]{}));
        pluralreductionrules.add(new Rule("eis", 2, "el", new String[]{}));
        pluralreductionrules.add(new Rule("�is", 2, "ol", new String[]{}));
        pluralreductionrules.add(new Rule("les", 3, "l", new String[]{}));
        pluralreductionrules.add(new Rule("res", 3, "r", new String[]{}));
        pluralreductionrules.add(new Rule("is", 2, "il", new String[]{"l�pis", "cais", "mais", "cr�cis", "biqu�nis", "pois", "depois", "dois", "leis"}));
        pluralreductionrules.add(new Rule("ns", 1, "m", new String[]{}));
        pluralreductionrules.add(new Rule("s", 2, "", new String[]{"ali�s", "pires", "l�pis", "cais", "mais", "mas", "menos",
            "f�rias", "fezes", "p�sames", "cr�cis", "g�s",
            "atr�s", "mois�s", "atrav�s", "conv�s", "�s",
            "pa�s", "ap�s", "ambas", "ambos", "messias"}));
    }

    private void fillFeminine() {
        femininereductionrules.add(new Rule("eira", 3, "eiro", new String[]{"beira", "cadeira", "frigideira", "bandeira", "feira", "capoeira", "barreira", "fronteira", "besteira", "poeira"}));
        femininereductionrules.add(new Rule("�aca", 3, "�aco", new String[]{}));
        femininereductionrules.add(new Rule("inha", 3, "inho", new String[]{"rainha", "linha", "minha"}));
        femininereductionrules.add(new Rule("osa", 3, "oso", new String[]{"mucosa", "prosa"}));
        femininereductionrules.add(new Rule("ica", 3, "ico", new String[]{"dica"}));
        femininereductionrules.add(new Rule("ada", 2, "ado", new String[]{"pitada"}));
        femininereductionrules.add(new Rule("ida", 3, "ido", new String[]{"vida"}));
        femininereductionrules.add(new Rule("�da", 3, "ido", new String[]{"reca�da", "sa�da", "d�vida"}));
        femininereductionrules.add(new Rule("ima", 3, "imo", new String[]{"v�tima"}));
        femininereductionrules.add(new Rule("iva", 3, "ivo", new String[]{"saliva", "oliva"}));
        femininereductionrules.add(new Rule("ona", 3, "�o", new String[]{"abandona", "lona", "iona", "cortisona", "mon�tona", "maratona", "acetona", "detona", "carona"}));
        femininereductionrules.add(new Rule("ora", 3, "or", new String[]{}));
        femininereductionrules.add(new Rule("esa", 3, "�s", new String[]{"mesa", "obesa", "princesa", "turquesa", "ilesa", "pesa", "presa"}));
        femininereductionrules.add(new Rule("na", 4, "no", new String[]{"carona", "abandona", "lona", "iona", "cortisona", "mon�tona", "maratona", "acetona", "detona", "guiana", "campana", "grana", "caravana", "banana", "paisana"}));
        femininereductionrules.add(new Rule("�", 2, "�o", new String[]{"amanh�", "arapu�", "f�", "div�"}));
    }

    private void fillAugmentativeDiminutive() {
        augmentativediminutivereductionrules.add(new Rule("ab�l�ssimo", 5, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("d�ssimo", 5, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("quinho", 4, "c", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("�ssimo", 3, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("�rrimo", 4, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("adinho", 3, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("zarr�o", 3, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("zinho", 2, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("�simo", 3, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("uinho", 4, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("alh�o", 4, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("arraz", 4, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("arr�o", 4, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("inho", 3, "", new String[]{"caminho", "cominho"}));
        augmentativediminutivereductionrules.add(new Rule("id�o", 4, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("ad�o", 4, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("�zio", 3, "", new String[]{"top�zio"}));
        augmentativediminutivereductionrules.add(new Rule("arra", 3, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("u�a", 4, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("a�o", 4, "", new String[]{"antebra�o"}));
        augmentativediminutivereductionrules.add(new Rule("a�a", 4, "", new String[]{}));
        augmentativediminutivereductionrules.add(new Rule("z�o", 2, "", new String[]{"coaliz�o"}));
        augmentativediminutivereductionrules.add(new Rule("�o", 3, "", new String[]{"camar�o", "chimarr�o", "can��o", "cora��o", "embri�o", "grot�o", "glut�o",
            "fic��o", "fog�o", "fei��o", "furac�o", "gam�o", "lampi�o", "le�o", "macac�o", "na��o",
            "�rf�o", "org�o", "patr�o", "port�o", "quinh�o", "rinc�o", "tra��o",
            "falc�o", "espi�o", "mam�o", "foli�o", "cord�o", "aptid�o", "campe�o",
            "colch�o", "lim�o", "leil�o", "mel�o", "bar�o", "milh�o", "bilh�o", "fus�o",
            "crist�o", "ilus�o", "capit�o", "esta��o", "sen�o"}));
    }

    private void fillAdverb() {
        adverbreductionrules.add(new Rule("mente", 4, "", new String[]{"experimente"}));
    }

    private void fillNoun() {
        nounreductionrules.add(new Rule("encialista", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("abilidade", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("icionista", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("cionista", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("al�stico", 3, "", new String[]{}));
        nounreductionrules.add(new Rule("ionista", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("ividade", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("iamento", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("alizado", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("atizado", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("ivismo", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("alismo", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("�stico", 4, "", new String[]{"eclesi�stico"}));
        nounreductionrules.add(new Rule("encial", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("at�ria", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("atoria", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("alista", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("amento", 3, "", new String[]{"firmamento", "fundamento", "departamento"}));
        nounreductionrules.add(new Rule("imento", 3, "", new String[]{}));
        nounreductionrules.add(new Rule("�utico", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("�utico", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("edouro", 3, "", new String[]{}));
        nounreductionrules.add(new Rule("queiro", 3, "", new String[]{}));
        nounreductionrules.add(new Rule("aliza�", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("atiza�", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("tizado", 4, "", new String[]{"alfabetizado"}));
        nounreductionrules.add(new Rule("adeiro", 4, "", new String[]{"desfiladeiro"}));
        nounreductionrules.add(new Rule("quice", 4, "c", new String[]{}));
        nounreductionrules.add(new Rule("�tico", 3, "", new String[]{}));
        nounreductionrules.add(new Rule("ionar", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("tiza�", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("idade", 5, "", new String[]{"autoridade", "comunidade"}));
        nounreductionrules.add(new Rule("mento", 6, "", new String[]{"firmamento", "elemento", "complemento", "instrumento", "departamento"}));
        nounreductionrules.add(new Rule("izado", 4, "", new String[]{"organizado", "pulverizado"}));
        nounreductionrules.add(new Rule("ativo", 4, "", new String[]{"pejorativo", "relativo"}));
        nounreductionrules.add(new Rule("ional", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("�ncia", 3, "", new String[]{}));
        nounreductionrules.add(new Rule("�ncia", 4, "", new String[]{"ambul�ncia"}));
        nounreductionrules.add(new Rule("iza�", 5, "", new String[]{"organiza�"}));
        nounreductionrules.add(new Rule("�rio", 3, "", new String[]{"volunt�rio", "sal�rio", "anivers�rio", "di�rio", "lion�rio", "arm�rio"}));
        nounreductionrules.add(new Rule("�rio", 6, "", new String[]{}));
        nounreductionrules.add(new Rule("auta", 5, "", new String[]{}));
        nounreductionrules.add(new Rule("�nse", 6, "", new String[]{}));
        nounreductionrules.add(new Rule("esco", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("ante", 2, "", new String[]{"gigante", "elefante", "adiante", "possante", "instante", "restaurante"}));
        nounreductionrules.add(new Rule("oria", 4, "", new String[]{"categoria"}));
        nounreductionrules.add(new Rule("ista", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("�aco", 3, "", new String[]{}));
        nounreductionrules.add(new Rule("�vel", 2, "", new String[]{"af�vel", "razo�vel", "pot�vel", "vulner�vel"}));
        nounreductionrules.add(new Rule("�vel", 5, "", new String[]{"poss�vel"}));
        nounreductionrules.add(new Rule("ente", 4, "", new String[]{"freq�ente", "alimente", "acrescente", "permanente", "oriente", "aparente"}));
        nounreductionrules.add(new Rule("inal", 3, "", new String[]{}));
        nounreductionrules.add(new Rule("tivo", 4, "", new String[]{"relativo"}));
        nounreductionrules.add(new Rule("tico", 3, "", new String[]{"pol�tico", "eclesi�stico", "diagnostico", "pr�tico", "dom�stico", "diagn�stico", "id�ntico", "alop�tico", "art�stico", "aut�ntico", "ecl�tico", "cr�tico", "critico"}));
        nounreductionrules.add(new Rule("agem", 3, "", new String[]{"coragem", "chantagem", "vantagem", "carruagem"}));
        nounreductionrules.add(new Rule("ador", 3, "", new String[]{}));
        nounreductionrules.add(new Rule("edor", 3, "", new String[]{}));
        nounreductionrules.add(new Rule("ural", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("idor", 4, "", new String[]{"ouvidor"}));
        nounreductionrules.add(new Rule("eiro", 3, "", new String[]{"desfiladeiro", "pioneiro", "mosteiro"}));
        nounreductionrules.add(new Rule("ismo", 3, "", new String[]{"cinismo"}));
        nounreductionrules.add(new Rule("eza", 3, "", new String[]{}));
        nounreductionrules.add(new Rule("ico", 4, "", new String[]{"tico", "p�blico", "explico"}));
        nounreductionrules.add(new Rule("dor", 4, "", new String[]{"ouvidor"}));
        nounreductionrules.add(new Rule("sor", 4, "", new String[]{"assessor"}));
        nounreductionrules.add(new Rule("tor", 3, "", new String[]{"benfeitor", "leitor", "editor", "pastor", "produtor", "promotor", "consultor"}));
        nounreductionrules.add(new Rule("ice", 4, "", new String[]{"c�mplice"}));
        nounreductionrules.add(new Rule("ano", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("ura", 4, "", new String[]{"imatura", "acupuntura", "costura"}));
        nounreductionrules.add(new Rule("ual", 3, "", new String[]{"bissexual", "virtual", "visual", "pontual"}));
        nounreductionrules.add(new Rule("ial", 3, "", new String[]{}));
        nounreductionrules.add(new Rule("bil", 6, "vel", new String[]{}));
        nounreductionrules.add(new Rule("rio", 5, "", new String[]{"volunt�rio", "sal�rio", "anivers�rio", "di�rio", "compuls�rio", "lion�rio", "pr�prio", "st�rio", "arm�rio"}));
        nounreductionrules.add(new Rule("ivo", 4, "", new String[]{"passivo", "possessivo", "pejorativo", "positivo"}));
        nounreductionrules.add(new Rule("ado", 2, "", new String[]{"grado"}));
        nounreductionrules.add(new Rule("ido", 3, "", new String[]{"c�ndido", "consolido", "r�pido", "decido", "t�mido", "duvido", "marido"}));
        nounreductionrules.add(new Rule("oso", 3, "", new String[]{"precioso"}));
        nounreductionrules.add(new Rule("vel", 5, "", new String[]{"poss�vel", "vulner�vel", "sol�vel"}));
        nounreductionrules.add(new Rule("a�", 3, "", new String[]{"equa�", "rela�"}));
        nounreductionrules.add(new Rule("i�", 3, "", new String[]{"elei��o"}));
        nounreductionrules.add(new Rule("�s", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("ez", 4, "", new String[]{}));
        nounreductionrules.add(new Rule("al", 4, "", new String[]{"afinal", "animal", "estatal", "bissexual", "desleal", "fiscal", "formal", "pessoal", "liberal", "postal", "virtual", "visual", "pontual", "sideral", "sucursal"}));
        nounreductionrules.add(new Rule("or", 2, "", new String[]{"motor", "melhor", "redor", "rigor", "sensor", "tambor", "tumor", "assessor", "benfeitor", "pastor", "terior", "favor", "autor"}));
    }

    private void fillVerb() {
        verbreductionrules.add(new Rule("ar�amo", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("�ssemo", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("er�amo", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("ir�amo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("�ssemo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("�ssemo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("�ramo", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("aremo", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("ariam", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("ar�ei", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("�ssei", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("assem", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("�vamo", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("�ramo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("eremo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("eriam", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("er�ei", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("�ssei", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("essem", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("�ramo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("iremo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("iriam", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("ir�ei", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("�ssei", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("issem", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("tizar", 4, "", new String[]{"alfabetizar"}));
        verbreductionrules.add(new Rule("ando", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("endo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("indo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("ondo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("aram", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("arde", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("�rei", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("arei", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("ar�o", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("er�o", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("ir�o", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("iona", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("arem", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("aria", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("armo", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("asse", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("aste", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("avam", 2, "", new String[]{"agravam"}));
        verbreductionrules.add(new Rule("�vei", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("eram", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("erde", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("erei", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("�rei", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("erem", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("eria", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("ermo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("esse", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("este", 3, "", new String[]{"faroeste", "agreste"}));
        verbreductionrules.add(new Rule("�amo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("iram", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("�ram", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("irde", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("irei", 3, "", new String[]{"admirei"}));
        verbreductionrules.add(new Rule("irem", 3, "", new String[]{"adquirem"}));
        verbreductionrules.add(new Rule("iava", 4, "", new String[]{"ampliava"}));
        verbreductionrules.add(new Rule("iria", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("irmo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("isse", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("iste", 4, "", new String[]{}));
        verbreductionrules.add(new Rule("izar", 5, "", new String[]{"organizar"}));
        verbreductionrules.add(new Rule("itar", 5, "", new String[]{"acreditar", "explicitar", "estreitar"}));
        verbreductionrules.add(new Rule("amo", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("ara", 2, "", new String[]{"arara", "prepara"}));
        verbreductionrules.add(new Rule("ar�", 2, "", new String[]{"alvar�"}));
        verbreductionrules.add(new Rule("are", 2, "", new String[]{"prepare"}));
        verbreductionrules.add(new Rule("ava", 2, "", new String[]{"agrava"}));
        verbreductionrules.add(new Rule("emo", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("eou", 5, "", new String[]{}));
        verbreductionrules.add(new Rule("era", 3, "", new String[]{"acelera", "espera"}));
        verbreductionrules.add(new Rule("er�", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("ere", 3, "", new String[]{"espere"}));
        verbreductionrules.add(new Rule("iam", 3, "", new String[]{"enfiam", "ampliam", "elogiam", "ensaiam"}));
        verbreductionrules.add(new Rule("�ei", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("imo", 3, "", new String[]{"reprimo", "intimo", "�ntimo", "nimo", "queimo", "ximo"}));
        verbreductionrules.add(new Rule("ira", 3, "", new String[]{"fronteira", "s�tira"}));
        verbreductionrules.add(new Rule("ir�", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("ire", 3, "", new String[]{"adquire"}));
        verbreductionrules.add(new Rule("�do", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("omo", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("ear", 4, "", new String[]{"alardear", "nuclear"}));
        verbreductionrules.add(new Rule("uei", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("u�a", 5, "u", new String[]{}));
        verbreductionrules.add(new Rule("ai", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("am", 2, "", new String[]{}));
        verbreductionrules.add(new Rule("ar", 2, "", new String[]{"azar", "bazaar", "patamar"}));
        verbreductionrules.add(new Rule("ei", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("em", 2, "", new String[]{"alem", "virgem"}));
        verbreductionrules.add(new Rule("er", 2, "", new String[]{"�ter", "pier"}));
        verbreductionrules.add(new Rule("eu", 3, "", new String[]{"chapeu"}));
        verbreductionrules.add(new Rule("ia", 3, "", new String[]{"est�ria", "fatia", "acia", "praia", "elogia", "mania", "l�bia", "aprecia", "pol�cia", "arredia", "cheia", "�sia"}));
        verbreductionrules.add(new Rule("ir", 3, "", new String[]{"freir"}));
        verbreductionrules.add(new Rule("ia", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("iu", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("ou", 3, "", new String[]{}));
        verbreductionrules.add(new Rule("i", 3, "", new String[]{}));
    }

    private void fillVowel() {
        vowelremovalrules.add(new Rule("a", 3, "", new String[]{"�sia"}));
        vowelremovalrules.add(new Rule("�", 3, "", new String[]{}));
        vowelremovalrules.add(new Rule("e", 3, "", new String[]{}));
        vowelremovalrules.add(new Rule("�", 3, "", new String[]{"beb�"}));
        vowelremovalrules.add(new Rule("o", 3, "", new String[]{"�o"}));
        vowelremovalrules.add(new Rule("bil", 2, "vel", new String[]{}));
        vowelremovalrules.add(new Rule("gue", 3, "g", new String[]{"gangue", "jegue"}));
    }
}
