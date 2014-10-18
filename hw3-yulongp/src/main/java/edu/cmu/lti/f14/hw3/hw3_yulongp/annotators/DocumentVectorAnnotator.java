package edu.cmu.lti.f14.hw3.hw3_yulongp.annotators;

import java.util.*;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f14.hw3.hw3_yulongp.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_yulongp.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_yulongp.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {

    FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
    if (iter.isValid()) {
      iter.moveToNext();
      Document doc = (Document) iter.get();
      createTermFreqVector(jcas, doc);
    }

  }

  /**
   * A basic white-space tokenizer, it deliberately does not split on punctuation!
   *
   * @param doc
   *          input text
   * @return a list of tokens.
   */

  List<String> tokenize0(String doc) {
    List<String> res = new ArrayList<String>();

    for (String s : doc.split("\\s+"))
      res.add(s);
    return res;
  }

  /**
   * Counting the frequency for each term in the sentence/document 
   * and then store the information into the JCas and Document 
   * @param jcas
   * @param doc
   */

  private void createTermFreqVector(JCas jcas, Document doc) {

    String docText = doc.getText();
    
    List<String> tokens = tokenize0(docText);

    // Using HashMap to store the <term, frequency> pair
    Map<String, Integer> termFreq = new HashMap<String, Integer>();

    for (String s : tokens) {
      if (termFreq.containsKey(s)) {
        int val = termFreq.get(s);
        termFreq.put(s, val + 1);
      } else {
        termFreq.put(s, 1);
      }
    }

    // Store term and frequency information into a token list
    List<Token> tokenList = new ArrayList<Token>();
    for (String key : termFreq.keySet()) {
      Token t = new Token(jcas);
      t.setText(key);
      t.setFrequency(termFreq.get(key));
      tokenList.add(t);
    }

    // Store the token list into the Document and JCas
    doc.setTokenList(Utils.fromCollectionToFSList(jcas, tokenList));
  }

}
