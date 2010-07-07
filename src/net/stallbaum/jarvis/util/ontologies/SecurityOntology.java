/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

import jade.content.onto.BasicOntology;
import jade.content.onto.Introspector;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;

/**
 * @author sean
 *
 */
public class SecurityOntology extends Ontology implements SecurityVocabulary {

	// ----------> The name identifying this ontology
	public static final String ONTOLOGY_NAME = "Bank-Ontology";

	// ----------> The singleton instance of this ontology
	private static Ontology instance = new SecurityOntology();

	// ----------> Method to access the singleton ontology object
	public static Ontology getInstance() { return instance; }

	private SecurityOntology() {
		super(ONTOLOGY_NAME, BasicOntology.getInstance());

		try {

			// ------- Add Concepts

			// Account
			ConceptSchema cs = new ConceptSchema(ROBOT);
			// add(cs, Account.class);
			cs.add(ROBOT_ID,
					(PrimitiveSchema) getSchema(BasicOntology.INTEGER),
					ObjectSchema.MANDATORY);
			cs.add(ROBOT_NAME,
					(PrimitiveSchema) getSchema(BasicOntology.STRING),
					ObjectSchema.MANDATORY);
		
		// ------- Add AgentActions

		// MakeRobotOperation		
		AgentActionSchema as = new AgentActionSchema(MAKE_ROBOT_OPERATION);
		add(as, MakeRobotOperation.class);
		as.add(MAKE_ROBOT_OPERATION_TYPE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
		} catch (OntologyException e) {

		}
	}
}
