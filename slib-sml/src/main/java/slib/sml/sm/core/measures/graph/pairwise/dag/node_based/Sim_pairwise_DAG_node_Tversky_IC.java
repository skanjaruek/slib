/*

Copyright or © or Copr. Ecole des Mines d'Alès (2012) 

This software is a computer program whose purpose is to 
process semantic graphs.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

 */
 
 
package slib.sml.sm.core.measures.graph.pairwise.dag.node_based;

import slib.sglib.model.graph.elements.V;
import slib.sml.sm.core.utils.SM_Engine;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.ex.SLIB_Exception;


/**
 * 
 * @author Sebastien Harispe
 * 
 */
public class Sim_pairwise_DAG_node_Tversky_IC implements Sim_DAG_node_abstract{
	
	
	public static final String alpha_param_name = "alpha";
	public static final String beta_param_name  = "beta";
	
	private double alpha = 0.5;
	private double beta  = 0.5;

	public Sim_pairwise_DAG_node_Tversky_IC() {}
	
	public Sim_pairwise_DAG_node_Tversky_IC(double alpha, double beta){
		this.alpha = alpha;
		this.beta  = beta;
	}

	
	
	public double sim(V a, V b, SM_Engine c, SMconf conf) throws SLIB_Exception {
		
		if(conf != null && conf.containsParam(alpha_param_name))
			alpha = conf.getParamAsDouble(alpha_param_name);
		
		if(conf != null && conf.containsParam(beta_param_name))
			beta = conf.getParamAsDouble(beta_param_name);
		
		double ic_a = c.getIC(conf.getICconf(),a);
		double ic_b = c.getIC(conf.getICconf(),b);
		double ic_MICA = c.getIC_MICA(conf.getICconf(),a,b);
		
		return sim(ic_a,ic_b,ic_MICA);
	}

	public double sim(double ic_a, double ic_b, double ic_mica) throws SLIB_Ex_Critic {
		
		if(ic_mica > ic_a || ic_mica > ic_b )
			throw new SLIB_Ex_Critic("Wrong parameters used with Tversky measure. " +
					"IC MICA must be inferior to IC(a) and IC(b)");

		double j = 0.;
		
		//System.out.println(alpha+" -- "+beta);
		
		if(ic_mica != 0)
			j = ( ic_mica ) / (  alpha * (ic_a-ic_mica) + beta * (ic_b-ic_mica) + ic_mica );
		
		return j;
	}


}




