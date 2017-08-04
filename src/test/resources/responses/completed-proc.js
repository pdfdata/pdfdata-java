{
  "type": "proc",
  "id": "proc_1555580e8ff",
  "created": "2016-06-15T19:19:19Z",
  "source_tags": [],
  "operations": [
    {
      "op": "images"
    },
    {
      "op": "text"
    },
    {
      "op": "text",
      "layout": "decompose"
    }
  ],
  "status": "complete",
  "documents": [
    {
      "type": "doc",
      "id": "doc_8e96ec0533ac3e1e988b7d1ca27bfdc096b82ddc",
      "filename": "annual_report.pdf",
      "tags": [
        "acquired:2017-08-02"
      ],
      "created": "2017-08-02T07:53:47Z",
      "expires": "2017-09-01T07:53:47Z",
      "pagecount": 1,
      "results": [
        {
          "op": "images",
          "data": [
            {
              "type": "page",
              "dimensions": [
                595,
                839
              ],
              "pagenum": 0,
              "images": [
                {
                  "type": "img",
                  "bounds": [
                    62.362,
                    248.541,
                    72.362,
                    255.541
                  ],
                  "resource": "rsrc_07a70ad3fca78c161846d0931058b6582c2ed94a"
                },
                {
                  "type": "img",
                  "bounds": [
                    62.362,
                    173.397,
                    63.362,
                    174.397
                  ],
                  "resource": "rsrc_9d7b8cffdb355edf6513a435e5465bbf7181ae72"
                }
              ]
            }
          ],
          "resources": {
            "rsrc_07a70ad3fca78c161846d0931058b6582c2ed94a": {
              "url": "/v1/resources/rsrc_07a70ad3fca78c161846d0931058b6582c2ed94a",
              "format": "png",
              "mimetype": "image/png",
              "dimensions": [
                10,
                7
              ]
            },
            "rsrc_9d7b8cffdb355edf6513a435e5465bbf7181ae72": {
              "url": "/v1/resources/rsrc_9d7b8cffdb355edf6513a435e5465bbf7181ae72",
              "format": "png",
              "mimetype": "image/png",
              "dimensions": [
                1,
                1
              ]
            }
          }
        },
        {
          "op": "text",
          "data": [
            {
              "type": "page",
              "dimensions": [
                595,
                839
              ],
              "pagenum": 0,
              "text": "foo"
            }
          ]
        },
        {
          "op": "text",
          "data": [
            {
              "type": "page",
              "dimensions": [
                595,
                839
              ],
              "pagenum": 0,
              "text": "foo"
            }
          ]
        }
      ]
    }
  ]
}
