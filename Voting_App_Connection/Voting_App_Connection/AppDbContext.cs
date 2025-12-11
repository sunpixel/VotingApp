using Microsoft.EntityFrameworkCore;


namespace Voting_App_Connection
{
    public class AppDbContext : DbContext
    {

        public DbSet<Vote> Votes { get; set; } = null!;

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            var connectionString = "Host=localhost;Port=5234;Database=TestDb;Username=postgres;Password=8o6i.V]B<pT8}yF1";

            optionsBuilder.UseNpgsql(connectionString);
        }

    }

    public class Vote
    {
        public int Id { get; set; }
        public string? Name { get; set; }
        public string? Description { get; set; }
        public byte? Photo {  get; set; }
        public int NumberOfVotes { get; set; }
        public string[]? WhoVoted { get; set; }
    }

}
