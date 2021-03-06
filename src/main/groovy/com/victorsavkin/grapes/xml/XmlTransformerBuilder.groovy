package com.victorsavkin.grapes.xml

class XmlTransformerBuilder {

	private Map parsedTransformers = [:]
	private lastTransformer
	private lastCondition
	private factory = new XmlTransformerFactory()

	XmlTransformer forEach(Closure callback) {
		lastTransformer = new XmlTransformer(callback)
	}

	XmlTransformer remove(Closure dsl) {
		def builder = new CriteriaBuilder()
		dsl.delegate = builder
		dsl()

		def criteria = builder.create()
		lastTransformer = factory.createRemoveElementsTransformer(criteria)
	}

	void when(Closure dsl){
		def builder = new CriteriaBuilder()
		dsl.delegate = builder
		dsl.resolveStrategy = Closure.DELEGATE_FIRST
		dsl()
		lastCondition = builder.create()
	}

	XmlTransformer then(Closure dsl){
		assert lastCondition, 'When block is not defined'
		lastTransformer = factory.createUpdateTransformer(lastCondition, dsl)
	}

	def propertyMissing(String name, XmlTransformer value) {
		assert value, 'Passed XmlTransformer is null'
		parsedTransformers[name] = value
	}

	static Map<String, XmlTransformer> transformations(Closure dsl){
		def builder = new XmlTransformerBuilder()
		dsl.delegate = builder
		dsl.resolveStrategy = Closure.DELEGATE_FIRST
		dsl()
		builder.parsedTransformers + [last: builder.lastTransformer]
	}
}

