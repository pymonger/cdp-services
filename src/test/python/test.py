import sys, httplib2, logging, time
import simplejson as json

http = httplib2.Http('.cache')

logger = logging.getLogger()

serviceUrl = 'http://localhost:5000/services/cdp_jena'
headers = {'Content-type':'application/x-www-form-urlencoded'}


def queryTDB(sparql):
    url = serviceUrl + '/query_triples'
    response = None
    content = None
    response, content = http.request(url, 'POST', sparql, headers)
    #print response
    j = json.loads(content)
    return j['triples']

def getInferred(triples, rules):
    url = serviceUrl + '/infer_this'
    resonse = None
    content = None
    reqJson = {'triples': triples, 'rules': rules}
    #print json.dumps(reqJson)
    #f = open('getInferred.json', 'w')
    #f.write(json.dumps(reqJson, indent=4))
    #f.close()
    response, content = http.request(url, 'POST', json.dumps(reqJson), headers)
    #print response
    j = json.loads(content)
    return j['triples']

def query(triples, sparql):
    url = serviceUrl + '/query_this'
    resonse = None
    content = None
    reqJson = {'triples': triples, 'sparql': sparql}
    #f = open('query.json', 'w')
    #f.write(json.dumps(reqJson, indent=4))
    #f.close()
    response, content = http.request(url, 'POST', json.dumps(reqJson), headers)
    #print response
    j = json.loads(content)
    return j['arqResults']
    
if __name__ == "__main__":
    sparqlTriples = '''PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX opmo: <http://openprovenance.org/model/opmo#>
PREFIX opmv: <http://purl.org/net/opmv/ns#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

SELECT ?sub ?pred ?obj WHERE {
  ?sub ?pred ?obj .
  ?sub a opmv:Artifact .
}'''

    rules = '''@prefix opmv:    http://purl.org/net/opmv/ns#
@prefix cdp:     http://provenance.jpl.nasa.gov/cdp#
@prefix rdf:     http://www.w3.org/1999/02/22-rdf-syntax-ns#
@prefix opmo:    http://openprovenance.org/model/opmo#

[airsVersion:
    (?artifact rdf:type opmv:Artifact)
    regex(str(?artifact), '.*AIRS\..*(v.)\..*', ?vers)
    ->
    (?artifact cdp:hasVersion ?vers)
]'''

    sparqlAirs = '''PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX opmo: <http://openprovenance.org/model/opmo#>
PREFIX opmv: <http://purl.org/net/opmv/ns#>
PREFIX cdp:  <http://provenance.jpl.nasa.gov/cdp#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

SELECT ?art ?vers WHERE {
  ?art cdp:hasVersion ?vers .
}'''

    t1 = time.time()
    t =  queryTDB(sparqlTriples)
    t2 = time.time()
    print "Got %d triples from queryTDB(). Took %f seconds." % (len(t.split('\n')), t2-t1)
    t3 = time.time()
    i = getInferred(t, rules)
    t4 = time.time()
    print "Got %d triples from getInferred(). Took %f seconds." % (len(i.split('\n')), t4-t3)
    t5 = time.time()
    r = query(i, sparqlAirs)
    t6 = time.time()
    print "query() completed. Took %f seconds." % (t6-t5)
    
