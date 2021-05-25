PUT bank
{
  "settings": {
    "mapping.single_type": true
  },
  "mappings": {
    "doc": {
      "properties": {
        "date": {
          "type": "date",
          "format": "dd-MM-yyyy"
        },
        "type": { 
          "type": "join",
          "relations": {
            "customer": "deposite" 
          }
        }
      }
    }
  }
}

PUT bank/doc/ANIL
{
  "customerName": "ANIL",
  "city": "CALCUTTA",
  "type": {
    "name": "customer"
  }
}

PUT bank/doc/SUNIL
{
  "customerName": "SUNIL",
  "city": "DELHI",
  "type": {
    "name": "customer"
  }
}

PUT bank/doc/MEHUL
{
  "customerName": "MEHUL",
  "city": "BARODA",
  "type": {
    "name": "customer"
  }
}


PUT bank/doc/MANDAR
{
  "customerName": "MANDAR",
  "city": "PATNA",
  "type": {
    "name": "customer"
  }
}


PUT bank/doc/MADHURI
{
  "customerName": "MADHURI",
  "city": "NAGPUR",
  "type": {
    "name": "customer"
  }
}

PUT bank/doc/PRAMOD
{
  "customerName": "PRAMOD",
  "city": "NAGPUR",
  "type": {
    "name": "customer"
  }
}


PUT bank/doc/SANDIP
{
  "customerName": "SANDIP",
  "city": "SURAT",
  "type": {
    "name": "customer"
  }
}


PUT bank/doc/SHIVANI
{
  "customerName": "SHIVANI",
  "city": "BOMBAY",
  "type": {
    "name": "customer"
  }
}

PUT bank/doc/KRANTI
{
  "customerName": "KRANTI",
  "city": "BOMBAY",
  "type": {
    "name": "customer"
  }
}

PUT bank/doc/NAREN
{
  "customerName": "NAREN",
  "city": "BOMBAY",
  "type": {
    "name": "customer"
  }
}

============================



PUT bank/doc/100?routing=ANIL
{
  "depositeId": 100,
  "customerName": "ANIL",
  "branch": "VRCE",
  "amount": 1000,
  "date": "01-03-1995",
  "type": {
    "name": "deposite",
    "parent": "ANIL"
  }
}

PUT bank/doc/101?routing=SUNIL
{
  "depositeId": 101,
  "customerName": "SUNIL",
  "branch": "AJNI",
  "amount": 5000,
  "date": "04-01-1998",
  "type": {
    "name": "deposite",
    "parent": "SUNIL"
  }
}


PUT bank/doc/102?routing=MEHUL
{
  "depositeId": 102,
  "customerName": "MEHUL",
  "branch": "KAROLBAGH",
  "amount": 3500,
  "date": "07-11-1995",
  "type": {
    "name": "deposite",
    "parent": "MEHUL"
  }
}


PUT bank/doc/104?routing=MADHURI
{
  "depositeId": 104,
  "customerName": "MADHURI",
  "branch": "CHANDNI",
  "amount": 1200,
  "date": "07-12-1995",
  "type": {
    "name": "deposite",
    "parent": "MADHURI"
  }
}



PUT bank/doc/105?routing=PRAMOD
{
  "depositeId": 105,
  "customerName": "PRAMOD",
  "branch": "VRCE",
  "amount": 3000,
  "date": "27-03-1996",
  "type": {
    "name": "deposite",
    "parent": "PRAMOD"
  }
}



PUT bank/doc/106?routing=SANDIP
{
  "depositeId": 106,
  "customerName": "SANDIP",
  "branch": "MGROAD",
  "amount": 2000,
  "date": "31-03-1996",
  "type": {
    "name": "deposite",
    "parent": "SANDIP"
  }
}



PUT bank/doc/107?routing=SHIVANI
{
  "depositeId": 107,
  "customerName": "SHIVANI",
  "branch": "ANDHERI",
  "amount": 1000,
  "date": "05-09-1995",
  "type": {
    "name": "deposite",
    "parent": "SHIVANI"
  }
}



PUT bank/doc/108?routing=KRANTI
{
  "depositeId": 108,
  "customerName": "KRANTI",
  "branch": "NEHRUPLACE",
  "amount": 5000,
  "date": "02-07-1995",
  "type": {
    "name": "deposite",
    "parent": "KRANTI"
  }
}


PUT bank/doc/109?routing=NAREN
{
  "depositeId": 109,
  "customerName": "NAREN",
  "branch": "POWAI",
  "amount": 7000,
  "date": "010-08-1995",
  "type": {
    "name": "deposite",
    "parent": "NAREN"
  }
}



=================

Give all the details of customer Anil. (/api/customer?custName=?)
============================================================+
GET bank/doc/_search
{
  "query": {
    "term": {
      "customerName.keyword": {
        "value": "ANIL"
      }
    }
  }
}


Give name of depositors having same living city as Anil and having deposit amount greater than 2000.(/api/depositors?custName=?&depositeAmount=?)
============================================================+

GET bank/doc/_search
{
  "query": {
    "term": {
      "customerName.keyword": {
        "value": "ANIL"
      }
    }
  }
}

// CALCUTTA living city

GET bank/doc/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "has_parent": {
            "parent_type": "customer",
            "query": {
              "term": {
                "city.keyword": {
                  "value": "CALCUTTA"
                }
              }
            }
          }
        },
        {
          "range": {
            "amount": {
              "gte": 2000
            }
          }
        }
      ]
    }
  }
}


========================================================================================================================
Give deposit details  of Customer in same city where Pramod is living.
========================================================================================================================

GET bank/doc/_search
{
  "query": {
    "term": {
      "customerName.keyword": {
        "value": "PRAMOD"
      }
    }
  }
}

// NAGPUR living city

GET bank/doc/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "has_parent": {
            "parent_type": "customer",
            "query": {
              "term": {
                "city.keyword": {
                  "value": "NAGPUR"
                }
              }
            }
          }
        }
      ]
    }
  }
}




