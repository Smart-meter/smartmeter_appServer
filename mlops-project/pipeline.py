import argparse
import pathlib
import sys
import time

from ml_pipeline import config, dataset_factory, model_factory, utils

def pipeline_task(task_func):
    """Decorator that adds a boolean attribute to the given function,
        indicating that it is a pipeline task."""
    task_func.is_task = True
    return task_func
class MLPipeline:
    """ML training pipeline.

        Encapsulates all pipeline-related functionality.

        Attributes:
            logger (logging.Logger): Logger object.
            config (config): Pipeline configuration.
            tasks (List[Callable]): Pipeline tasks.
            timestamp (int): Unix time of run.
            artifact_dir (str): Path to artifact directory.
            dataset (dataset): Dataset object.
            model (model): Model object.
            idx_train (pd.Int64Index): Indices of training examples.
            idx_test (pd.Int64Index): Indices of test examples.
        """
    def __init__(
            self:"MLPipeline", project_config_path: str, logger: "logging.Logger")->None:
        """
        Initialises the pipeline.
        :param project_config_path:
        :param logger: Logger object
        """
        # load config file and get tasks
        project_config_path = pathlib.Path(project_config_path)
        logger.debug(project_config_path.parent)
        logger.debug(project_config_path.stem)
        self.config = config.Config(
            project_config_path.parent, project_config_path.stem
        )
        self.config.load()
        logger.debug(f"The configurations loaded{self.config.items}")
        # Build the pipeline by topologically sorting the task DAG
        sorted_tasks = self.topological_sort(self.config.items.tasks)
        self.tasks = []
        for task in sorted_tasks:
            try:
                func = getattr(self, task)
            except AttributeError:
                raise Exception(f"'{task}' is not defined in the pipeline.")

            if hasattr(func, "is_task"):
                self.tasks.append(func)
            else:
                raise Exception(f"'{func}' is not a pipeline task.")
        # create a Unix timestamp for the current run
        self.timestamp = int(time.time())
        logger.debug(self.timestamp)
        logger.debug(self.config)
        # create a directory to store training artifacts
        self.artifact_dir = (
            f"./artifacts/{self.config.project}/{self.timestamp}"
        )
        pathlib.Path(self.artifact_dir).mkdir(parents=True)

    @pipeline_task
    def load_data(self) -> None:
        # ...
        pass

    @pipeline_task
    def preprocess_data(self) -> None:
        # ...
        pass

    @pipeline_task
    def feature_engineer_data(self) -> None:
        pass

    @pipeline_task
    def train_model(self) -> None:
        pass

    @pipeline_task
    def evaluate_model(self) -> None:
        pass

    @pipeline_task
    def create_report(self) -> None:
        pass
    def not_a_task(self, a, b) -> None:
        # ...
        pass

    def run(self) -> None:
        for task in self.tasks:
            task()

    def topological_sort(self, digraph: "omegaconf.DictConfig"):
        """
        Topological sorting for the pipeline tasks from the configuration yaml file
        :param digraph:
        :return:
        """
        # calculate indegree for all nodes
        indegree = {node: 0 for node in digraph}
        for node in digraph:
            for adjacent_node in digraph[node].next:
                if adjacent_node not in indegree:
                    raise Exception(f"Node '{adjacent_node}' not defined")
                indegree[adjacent_node] += 1

        # get zero-indegree nodes
        zero_indegree_nodes = [node for node in digraph if indegree[node] == 0]

        # sort
        sorted_nodes = []
        while len(zero_indegree_nodes) > 0:
            # add a zero-indegree node to the sorted array
            node = zero_indegree_nodes.pop()
            sorted_nodes.append(node)

            # decrement the indegree of all adjacent nodes
            for adjacent_node in digraph[node].next:
                indegree[adjacent_node] -= 1
                if indegree[adjacent_node] == 0:
                    zero_indegree_nodes.append(adjacent_node)

        if len(sorted_nodes) != len(digraph):
            raise Exception("Tasks do not form a DAG")

        return sorted_nodes
if __name__ == "__main__":
    #Create the parser object, initializing it with the description of the program
    parser = argparse.ArgumentParser(
        description="Machine learning training pipeline.", allow_abbrev=False
    )
    #Add each of the command-line arguments to the parser
    parser.add_argument(
        "-c",
        "--config",
        type=str,
        help="path to project configuration file",
        required=True,
    )
    parser.add_argument(
        "-d", "--debug", action="store_true", help="run in debug mode"
    )
    #Parse the input arguments
    args = parser.parse_args()
    logger = utils.Logger("ml-pipeline", debug=args.debug).get()
    logger.debug(args.config)
    #Create the pipeline and run it
    try:
        pipeline = MLPipeline(args.config, logger)
        pipeline.run()
    except Exception as error:
        logger.error(error)
        sys.exit(-1)

    logger.info(f"Pipeline run complete. See all artifacts in {pipeline.artifact_dir}")