package edu.cmu.lti.f14.hw3.hw3_yulongp.casconsumers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f14.hw3.hw3_yulongp.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_yulongp.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_yulongp.utils.Utils;

public class RetrievalEvaluator extends CasConsumer_ImplBase {

  /** query id number **/
  public ArrayList<Integer> qIdList;

  /** query and text relevant values **/
  public ArrayList<Integer> relList;

  /** original text for report **/
  public ArrayList<String> docText;

  /** frequency values **/
  public ArrayList<HashMap<String, Integer>> tokenFreq;

  /** record number of sentences for each query **/
  public ArrayList<Integer> numOfSent;

  public int numOfSentbyId;

  /** Name of the outputFile **/
  public String outputFile;
  
  public void initialize() throws ResourceInitializationException {

    qIdList = new ArrayList<Integer>();
    relList = new ArrayList<Integer>();
    docText = new ArrayList<String>();
    tokenFreq = new ArrayList<HashMap<String, Integer>>();
    numOfSent = new ArrayList<Integer>();
    numOfSentbyId = 0;
    outputFile = (String) getConfigParameterValue("OutputFile");
  }

  /**
   * Record the original text for the collection, record the query id and corresponding text
   * store the pair of <term, frequency> into a map and record the number of sentences for each query
   * @param CAS aCas 
   */
  @Override
  public void processCas(CAS aCas) throws ResourceProcessException {

    JCas jcas;
    try {
      jcas = aCas.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();

    if (it.hasNext()) {
      Document doc = (Document) it.next();

      // Make sure that your previous annotators have populated this in CAS
      FSList fsTokenList = doc.getTokenList();
      List<Token> tokenList = Utils.fromFSListToCollection(fsTokenList, Token.class);

      Map<String, Integer> tokenMap = new HashMap<String, Integer>();
      for (Token t : tokenList) {
        //if (!stopwords.contains(t.getText()))
        tokenMap.put(t.getText(), t.getFrequency());
      }

      int qid = doc.getQueryID();
      qIdList.add(qid);
      relList.add(doc.getRelevanceValue());
      docText.add(doc.getText());
      tokenFreq.add((HashMap<String, Integer>) tokenMap);

      if (doc.getRelevanceValue() == 99) {
        numOfSent.add(numOfSentbyId);
        numOfSentbyId = 0;
      } else {
        numOfSentbyId += 1;
      }
    }

  }

  /**
   * Compute Cosine Similarity and rank the retrieved sentences, 
   * compute the MRR metric and write the report file
   * @param ProcessTrace arg0
   */
  @Override
  public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
          IOException {

    super.collectionProcessComplete(arg0);

    // TODO :: compute the cosine similarity measure
    numOfSent.add(numOfSentbyId);
    int num = numOfSent.size();
    int index = 0;
    // numOfSent tokenFreq
    List<Integer> rankings = new ArrayList<Integer>();
    List<Double> similarity = new ArrayList<Double>();
    for (int i = 1; i < num; i++) {
      HashMap<String, Integer> query = tokenFreq.get(index);
      int totalNum = numOfSent.get(i);
      index += 1;
      similarity.add(0.0);
      List<Double> localSim = new ArrayList<Double>();
      for (int j = 0; j < totalNum; j++) {
        double d = computeCosineSimilarity(query, tokenFreq.get(index + j));
        similarity.add(d);
        System.out.println(d);
        localSim.add(d);
      }
      double sim = similarity.get(index);
      Collections.sort(localSim, Collections.reverseOrder());
      int rank = localSim.indexOf(sim) + 1;
      rankings.add(rank);
      index += totalNum;
    }
    
    index = 0;
    List<String> content = new ArrayList<String>();
    num = qIdList.size();
    for (int i = 1; i < num; i++) {
      if (relList.get(i) == 1) {
        String result = String.format("cosine=%.4f\trank=%d\tqid=%d\trel=1\t%s", similarity.get(i),
                rankings.get(index), qIdList.get(i), docText.get(i));
        content.add(result);
        index += 1;
      }
    }

    // TODO :: compute the metric:: mean reciprocal rank
    double metric_mrr = compute_mrr(rankings);
    System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);

    // Write the content into the report
    FileWriter fw = new FileWriter(outputFile, false);
    for (String s : content) {
      fw.write(s + "\n");
    }
    fw.write(String.format("MRR=%.4f\n", metric_mrr));
    fw.close();
  }

  /**
   * calculates the Euclidean norm of a vector
   * 
   * @param a
   *          Collection of Integers
   * @return Euclidean Norm of a vector of integers
   */
  private double normalize(Collection<Integer> vals) {
    double result = 0.0;
    for (Integer i : vals) {
      result += Math.pow(i, 2);
    }
    return Math.sqrt(result);
  }

  /**
   * Calculate cosine similarity between query and document
   * 
   * @param queryVector
   *          world vector of the query
   * @param docVector
   *          word vector of the document
   * @return Cosine similarity between query and document
   */
  private double computeCosineSimilarity(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double cosine_similarity = 0.0;
    double norm = normalize(queryVector.values()) * normalize(docVector.values());
    // Store the intersection of query vector and document vector
    HashSet<String> overloap = new HashSet<String>(queryVector.keySet());
    overloap.retainAll(docVector.keySet());
    for (String s : overloap) {
      cosine_similarity += queryVector.get(s) * docVector.get(s);
    }
    cosine_similarity = cosine_similarity / norm;
    return cosine_similarity;
  }

  /**
   * Compute the mean reciprocal rank (MRR) of the text collection
   * 
   * @param the
   *          ranking list for the text collection
   * @return mrr
   */
  private double compute_mrr(List<Integer> rankings) {
    double metric_mrr = 0.0;

    // TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
    for (int i = 0; i < rankings.size(); i++) {
      metric_mrr += 1.0 / rankings.get(i);
    }
    metric_mrr /= rankings.size();

    return metric_mrr;
  }

}
